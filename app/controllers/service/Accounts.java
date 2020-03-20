package controllers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbobjects.user.User;
import dbobjects.user.UserRepository;
import models.RequestResource;
import models.accounts.Registration;
import play.Logger;
import play.core.ObjectMapperComponents;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.ResponseGenerator;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class Accounts extends Controller {
    private final HttpExecutionContext ec;

    private final UserRepository userRepository;

    private final FormFactory formFactory;

    static ResponseGenerator rg;

    @Inject
    public Accounts(HttpExecutionContext ec, UserRepository userRepository, FormFactory formFactory) {
        this.ec = ec;
        this.userRepository = userRepository;
        this.formFactory = formFactory;
    }

    public CompletionStage<Result> register(final Http.Request request) {
        JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, Registration.class);
        RequestResource<Registration> resource = null;
        Registration r;
        try {
            resource = Json.mapper().readValue(request.body().asJson().toString(), javaType);
            Logger.error("3|" + resource.toString());
            r = resource.getPayload();
        } catch (Exception e) {
            Logger.error(e.toString());
            Result rr = rg.generatedResponse(resource,
                    "error parsing payload",
                    e.getMessage(),
                    "internalServerError");
            return CompletableFuture.completedFuture(rr);
        }

        return supplyAsync(() -> {
            return ok("register " + request.toString() + " " + request.toString());
        }, ec.current());
    }

    public CompletionStage<Result> registerAdmin(final Http.Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok(request.toString());
        }, ec.current());
    }

    public CompletionStage<Result> login(final Http.Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok(request.toString());
        }, ec.current());
    }

    public CompletionStage<Result> logout(final Http.Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok(request.toString());
        }, ec.current());
    }
}
