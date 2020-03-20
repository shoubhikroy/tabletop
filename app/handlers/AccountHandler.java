package handlers;

import dbobjects.user.User;
import dbobjects.user.UserRepository;
import models.ResponseResource;
import models.accounts.RegistrationInfo;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class AccountHandler {
    private final UserRepository repository;
    private final HttpExecutionContext ec;

    @Inject
    public AccountHandler(UserRepository repository, HttpExecutionContext ec) {
        this.repository = repository;
        this.ec = ec;
    }

    public CompletionStage<Optional<ResponseResource<String>>> register(RegistrationInfo registrationInfo) {
        User user = new User();
        user.setUsername(registrationInfo.getUsername());
        user.setEmail(registrationInfo.getEmail());
        user.setPassword(registrationInfo.getPassword());

        return repository.create(user).thenApplyAsync(_user -> {

            return _user.map(data -> new User(data, link(request, data)));
        }, ec.current());
    }
}
