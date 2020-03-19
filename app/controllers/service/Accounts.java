package controllers.service;

import dbobjects.user.User;
import dbobjects.user.UserRepository;
import models.RequestResource;
import models.accounts.Registration;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.libs.Json.toJson;

public class Accounts extends Controller {
    private final HttpExecutionContext ec;

    private final UserRepository userRepository;

    private final FormFactory formFactory;

    @Inject
    public Accounts(HttpExecutionContext ec, UserRepository userRepository, FormFactory formFactory) {
        this.ec = ec;
        this.userRepository = userRepository;
        this.formFactory = formFactory;
    }

    public CompletionStage<Result> register(final Http.Request request) {
        Logger.info("Registering user...");
        Object body = request.body();
        Logger.info(request.body().asJson().toPrettyString());
//
        try {
            RequestResource<Registration> resource = Json.fromJson(request.body().asJson(), RequestResource.class);
            return supplyAsync(() -> {
                return ok("register " + resource.toString() + " " + request.toString());
            }, ec.current());
        } catch (Error e) {
            return CompletableFuture.completedFuture(forbidden("Error"));
        }
        //User user = formFactory.form(User.class).bindFromRequest(request).get();
//
//        return userRepository
//                .create(user)
//                .thenApplyAsync(u -> ok(toJson(u)), ec.current());
//        return supplyAsync(() -> {
//            return ok("register " + request.toString() + " " + request.toString());
//        }, ec.current());
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
