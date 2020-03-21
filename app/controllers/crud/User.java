package controllers.crud;

import dbobjects.user.UserRepository;
import play.Logger;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.libs.Json.toJson;

public class User extends Controller {
    private final HttpExecutionContext ec;

    private final UserRepository userRepository;

    private final FormFactory formFactory;

    public User(HttpExecutionContext ec, UserRepository userRepository, FormFactory formFactory) {
        this.ec = ec;
        this.userRepository = userRepository;
        this.formFactory = formFactory;
    }

    public CompletionStage<Result> getUsers() {
        return userRepository
                .list()
                .thenApplyAsync(userStream -> ok(toJson(userStream.collect(Collectors.toList()))), ec.current());
    }

    public CompletionStage<Result> getUser(final Http.Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok(request.toString());
        }, ec.current());
    }

    public CompletionStage<Result> show(final Http.Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok(request.toString());
        }, ec.current());
    }

    public CompletionStage<Result> addUser(final Http.Request request) {
        Logger.info(request.toString());
        dbobjects.user.User user = formFactory.form(dbobjects.user.User.class).bindFromRequest(request).get();
        return userRepository
                .create(user)
                .thenApplyAsync(u -> ok(toJson(u)), ec.current());
//        return supplyAsync(() -> {
//            return ok(user.toString());
//        }, ec.current());
    }

    public CompletionStage<Result> update(final Http.Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok(request.toString());
        }, ec.current());
    }

    public CompletionStage<Result> delete(final Http.Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok(request.toString());
        }, ec.current());
    }
}
