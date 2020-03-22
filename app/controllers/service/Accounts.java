package controllers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import handlers.AccountHandler;
import play.Logger;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import handlers.ResponseHandler;

import javax.inject.Inject;
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

    public CompletionStage<Result> register(final Http.Request request) throws JsonProcessingException {
        return accountHandler.register(request).thenApplyAsync(response -> response, ec.current());
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
