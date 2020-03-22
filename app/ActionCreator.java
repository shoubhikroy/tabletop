import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import handlers.ExceptionHandler;
import models.RequestResource;
import play.Logger;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import handlers.ResourceHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.lang.reflect.Method;

import static interceptors.headers.JwtFilter.HEADER_AUTHORIZATION;

public class ActionCreator implements play.http.ActionCreator {

    static ResourceHandler rg;

    @Override
    public Action createAction(Http.Request request, Method actionMethod) {
        //deserialize all non GET calls -> they should here to request body structure
        //creates a request resource object
        //get calls create empty resource
        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Http.Request req) {
            JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, Object.class);
            RequestResource<Object> resource = new RequestResource<>("not_supplied_GET_call", req.uri(), null);
            if (request.hasBody()) {
                try {
                    resource = Json.mapper().readValue(request.body().asJson().toString(), javaType);
                } catch (Exception e) {
                    resource = new RequestResource<>("not_supplied", req.uri(), null);
                    return CompletableFuture.completedFuture(ExceptionHandler.baseErrors(e, resource));
                }
                // common payload errors:
                if (null == resource ||
                        null == resource.getPayload()) {
                    resource = new RequestResource<>("not_supplied", req.uri(), null);
                    Result rr = rg.generateResponse(resource, "Request Body Error", "Looks like empty data or missing payload", "badRequest");
                    return CompletableFuture.completedFuture(rr);
                }
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

                if (request.hasBody()) {
                    body = Json.mapper().convertValue(resource, JsonNode.class);
                    JsonNode payload = request.body().asJson().get("payload");
                    ((ObjectNode)body).put("payload", payload);
                }
                Logger.info("Entering method: " + req.uri());
            } catch (Exception e) {
                Logger.error(e.toString());
                Result rr = rg.generateResponse(resource,
                        "Error processing request",
                        e.getMessage(),
                        "internalServerError");
                return CompletableFuture.completedFuture(rr);
            }
            RequestResource<Object> finalResource = resource;


            try {
                return delegate.call(req.withBody(new Http.RequestBody(body)))
                        .exceptionally(throwable -> ExceptionHandler.baseErrors(throwable, finalResource));
            } catch (Exception e) {
                return CompletableFuture.completedFuture(ExceptionHandler.baseErrors(e, finalResource));
            }
            }
        };
    }
}