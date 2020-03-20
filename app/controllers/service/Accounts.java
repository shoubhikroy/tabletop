package controllers.service;

import com.fasterxml.jackson.databind.JavaType;
import handlers.AccountHandler;
import models.RequestResource;
import models.accounts.RegistrationInfo;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import handlers.ResponseHandler;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class Accounts extends Controller {
    private final HttpExecutionContext ec;

    private final AccountHandler accountHandler;

    private final FormFactory formFactory;

    static ResponseHandler rg;

    @Inject
    public Accounts(HttpExecutionContext ec, AccountHandler accountHandler, FormFactory formFactory) {
        this.ec = ec;
        this.accountHandler = accountHandler;
        this.formFactory = formFactory;
    }

    public CompletionStage<Result> register(final Http.Request request) {
        JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, RegistrationInfo.class);
        RequestResource<RegistrationInfo> resource = null;
        RegistrationInfo registrationInfo;

        //validation
        try {
            resource = Json.mapper().readValue(request.body().asJson().toString(), javaType);
        } catch (Exception e) {
            Logger.error(e.toString());
            Result rr = rg.generatedResponse(resource, "error parsing payload", e.getMessage(), "internalServerError");
            return CompletableFuture.completedFuture(rr);
        }

        registrationInfo = resource.getPayload();
        return accountHandler.register(registrationInfo).thenApplyAsync(posts -> {
            final List<PostResource> postList = posts.collect(Collectors.toList());
            return ok(Json.toJson(postList));
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
