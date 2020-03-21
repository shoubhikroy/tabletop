package controllers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import handlers.AccountHandler;
import interceptors.descriptors.WithErrors;
import models.RequestResource;
import models.accounts.RegistrationInfo;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import handlers.ResponseHandler;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@WithErrors
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

    public CompletionStage<Result> register(final Http.Request request) throws JsonProcessingException {
        JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, RegistrationInfo.class);
        RequestResource<RegistrationInfo> resource = Json.mapper().readValue(request.body().asJson().toString(), javaType);
        return accountHandler.list(resource).thenApplyAsync(response -> response, ec.current());
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
