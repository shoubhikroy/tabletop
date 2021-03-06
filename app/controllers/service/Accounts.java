package controllers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import handlers.AccountHandler;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import handlers.ResourceHandler;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static handlers.AccountHandler.*;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class Accounts extends Controller {
    private final HttpExecutionContext ec;

    private final AccountHandler accountHandler;

    private final FormFactory formFactory;

    static ResourceHandler rg;

    @Inject
    public Accounts(HttpExecutionContext ec, AccountHandler accountHandler, FormFactory formFactory) {
        this.ec = ec;
        this.accountHandler = accountHandler;
        this.formFactory = formFactory;
    }

    //POST
    //Parameters: username, password, email
    //Returns:    user
    public CompletionStage<Result> register(final Http.Request request) throws JsonProcessingException {
        return accountHandler.register(request, USER);
    }

    //POST
    //Parameters: username, password, email
    //Returns:    user
    public CompletionStage<Result> registerAdmin(final Http.Request request) throws JsonProcessingException {
        return accountHandler.register(request, ADMIN);
    }

    //POST
    //Parameters: username, password
    //Returns:    token
    public CompletionStage<Result> login(final Http.Request request) throws JsonProcessingException {
        return accountHandler.login(request);
    }

    //GET
    //Returns:    current user details
    public CompletionStage<Result> getUser(final Http.Request request) throws JsonProcessingException {
        return accountHandler.getUser(request);
    }

    //GET
    //Returns:    token
    public CompletionStage<Result> getActiveUsers(final Http.Request request) throws JsonProcessingException {
        return accountHandler.getActiveUsers(request);
    }
}
