import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import handlers.ExceptionHandler;
import jwt.Attrs;
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
        Logger.info("Entered ActionCreator");
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
                String username = "no+token";
                Integer roles = 0;
                String userId = "no+token";
                String ipAddress = req.remoteAddress();

                if (request.attrs().containsKey(Attrs.VERIFIED_JWT)) {
                    username = String.valueOf(request.attrs().get(Attrs.VERIFIED_JWT).getUsername());
                    roles = Integer.valueOf(request.attrs().get(Attrs.VERIFIED_JWT).getRoles());
                    userId = String.valueOf(request.attrs().get(Attrs.VERIFIED_JWT).getUserId());
                }

                if (null == resource.getHash() || resource.getHash().isEmpty()) resource.setHash(UUID.randomUUID().toString());
                resource.setUsername(username);
                resource.setRoles(roles);
                resource.setUserId(userId.toString());
                resource.setIpAddress(ipAddress);
                resource.setEndpoint(req.uri());
                body = Json.mapper().convertValue(resource, JsonNode.class);
                if (request.hasBody()) {
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