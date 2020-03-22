package handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.mysql.jdbc.log.Log;
import dbobjects.user.User;
import dbobjects.user.UserRepository;
import models.RequestResource;
import models.ResponseResource;
import models.accounts.RegistrationInfo;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;
import static play.mvc.Results.ok;

public class AccountHandler {
    private final UserRepository repository;
    private final HttpExecutionContext ec;
    private final UserRepository userRepository;
    static ResponseHandler rg;

    @Inject
    public AccountHandler(UserRepository repository, HttpExecutionContext ec, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.ec = ec;
    }

    public CompletionStage<Result> register(Http.Request _request) throws JsonProcessingException {
        JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, RegistrationInfo.class);
        RequestResource<RegistrationInfo> request = Json.mapper().readValue(_request.body().asJson().toString(), javaType);

        User user = new User();
        user.setUsername(request.getPayload().getUsername());
        user.setEmail(request.getPayload().getEmail());
        user.setPassword(request.getPayload().getPassword());

        return repository.create(user).thenApplyAsync(_user -> {

            Result rr = rg.generatedErrorResponse(request, "error", "errors", "badRequest");
            return rr;
        }, ec.current());
    }

    public CompletionStage<Result> list(RequestResource<RegistrationInfo> request) {
        User user = new User();
        user.setUsername(request.getPayload().getUsername());
        user.setEmail(request.getPayload().getEmail());
        user.setPassword(request.getPayload().getPassword());
        return repository.list()
            .thenApplyAsync(userStream -> ok(toJson(userStream.collect(Collectors.toList()))), ec.current());
//            Result rr = rg.generatedErrorResponse(request, "error", errors, "badRequest");
//            return rr;
//        }, ec.current());
    }
}
