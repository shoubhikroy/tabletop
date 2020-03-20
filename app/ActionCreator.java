import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.RequestResource;
import models.accounts.Registration;
import play.Logger;
import play.libs.Json;
import play.libs.typedmap.TypedMap;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import services.ResponseGenerator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import java.lang.reflect.Method;

import static jwt.filter.JwtFilter.HEADER_AUTHORIZATION;

public class ActionCreator implements play.http.ActionCreator {

    static ResponseGenerator rg;

    @Override
    public Action createAction(Http.Request request, Method actionMethod) {
        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Http.Request req) {
                RequestResource<Object> resource = Json.fromJson(req.body().asJson(), RequestResource.class);

                // common payload errors:
                if (null == resource ||
                    null == resource.getPayload()) {
                    resource = new RequestResource<>("not_supplied", req.uri(), "");
                    Result rr = rg.generatedResponse(resource, "error", "Looks like empty data or missing payload", "badRequest");
                    return CompletableFuture.completedFuture(rr);
                }

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

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode body = mapper.convertValue(resource, JsonNode.class);

                    return delegate.call(req.withBody(new Http.RequestBody(body)));
                } catch (Exception e) {
                    Logger.error("Error pre parsing");
                    Result rr = rg.generatedResponse(resource,
                            "error",
                            "error pre_parsing request",
                            "internalServerError");
                    return CompletableFuture.completedFuture(rr);
                }
            }
        };
    }
}