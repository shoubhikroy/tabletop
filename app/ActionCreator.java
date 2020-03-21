import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.RequestResource;
import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import handlers.ResponseHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.lang.reflect.Method;

import static jwt.filter.JwtFilter.HEADER_AUTHORIZATION;

public class ActionCreator implements play.http.ActionCreator {

    static ResponseHandler rg;

    @Override
    public Action createAction(Http.Request request, Method actionMethod) {
        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Http.Request req) {
                JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, Object.class);
                RequestResource<Object> resource = null;
                try {
                    resource = Json.mapper().readValue(request.body().asJson().toString(), javaType);
                } catch (Exception e) {
                    Logger.error(e.toString());
                    resource = new RequestResource<>("not_supplied", req.uri(), null);
                    Result rr = rg.generatedErrorResponse(resource, "Looks like empty or bad data", e.getMessage(), "badRequest");
                    return CompletableFuture.completedFuture(rr);
                }
                // common payload errors:
                if (null == resource ||
                    null == resource.getPayload()) {
                    resource = new RequestResource<>("not_supplied", req.uri(), null);
                    Result rr = rg.generatedErrorResponse(resource, "error", "Looks like empty data or missing payload", "badRequest");
                    return CompletableFuture.completedFuture(rr);
                }

                JsonNode body = null;
                // massage payload
                try {
                    Http.Headers _headers = req.getHeaders();
                    Map<String, List<String>> headers = _headers.asMap();

                    List<String> authHeader =  headers.get(HEADER_AUTHORIZATION);

                    String username = "no+user";
                    String ipAddress = req.remoteAddress();

                    if (null == resource.getHash() || resource.getHash().isEmpty()) resource.setHash(UUID.randomUUID().toString());
                    resource.setUsername(username);
                    resource.setIpAddress(ipAddress);
                    resource.setEndpoint(req.uri());

                    body = Json.mapper().convertValue(resource, JsonNode.class);
                    JsonNode payload = request.body().asJson().get("payload");
                    ((ObjectNode)body).put("payload", payload);
                    Logger.info("Entering method: " + req.uri());
                } catch (Exception e) {
                    Logger.error(e.toString());
                    Result rr = rg.generatedErrorResponse(resource,
                            "Error processing request",
                            e.getMessage(),
                            "internalServerError");
                    return CompletableFuture.completedFuture(rr);
                }
                RequestResource<Object> finalResource = resource;
                return delegate.call(req.withBody(new Http.RequestBody(body))).exceptionally(throwable -> {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    throwable.printStackTrace(pw);
                    String sStackTrace = sw.toString();
                    Logger.error(sStackTrace);

                    String msg = throwable.getMessage();
                    while (null != throwable.getCause()) {
                        Throwable _throwable = throwable.getCause();
                        if (_throwable.getClass().toString().contains(".exceptions.jdbc4.")) {
                            msg = _throwable.getMessage();
                            break;
                        }
                        throwable = throwable.getCause();
                    }

                    Result rr = rg.generatedErrorResponse(finalResource, msg, sStackTrace, "internalServerError");
                    return rr;
                });
            }
        };
    }
}