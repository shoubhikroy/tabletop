package handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mysql.jdbc.log.Log;
import dbobjects.user.User;
import dbobjects.user.UserRepository;
import models.RequestResource;
import models.ResponseResource;
import models.accounts.RegistrationInfo;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

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

    public CompletionStage<Result> register(RequestResource<RegistrationInfo> request) {
        ArrayList<String> errors = new ArrayList<String>();
        User user = new User();
        user.setUsername(request.getPayload().getUsername());
        user.setEmail(request.getPayload().getEmail());
        user.setPassword(request.getPayload().getPassword());

        errors.add("Testing");
        return repository.create(user).thenApplyAsync(_user -> {
            Logger.error("GOT THIS FAR");
            Result rr = rg.generatedErrorResponse(request, "error", errors, "badRequest");
            return rr;
        }, ec.current());
    }
}
