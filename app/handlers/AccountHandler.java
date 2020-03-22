package handlers;

import cache.ActiveUsers;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import dbobjects.user.User;
import dbobjects.user.UserRepository;
import models.RequestResource;
import models.accounts.RegistrationInfo;
import models.accounts.LoginInfo;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;

public class AccountHandler {
    private final UserRepository repository;
    private final HttpExecutionContext ec;
    private final UserRepository userRepository;
    static ResourceHandler rg;
    private final ActiveUsers activeUsers;
    private Config config;

    public final static Integer ADMIN = 0;
    public final static Integer USER = 1;

    @Inject
    public AccountHandler(UserRepository repository, HttpExecutionContext ec, Config config,
                          UserRepository userRepository, ActiveUsers activeUsers) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.ec = ec;
        this.config = config;
        this.activeUsers = activeUsers;
    }

    public CompletionStage<Result> register(Http.Request _request, int type) throws JsonProcessingException {
        JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, RegistrationInfo.class);
        RequestResource<RegistrationInfo> request = Json.mapper().readValue(_request.body().asJson().toString(), javaType);

        User user = new User();
        user.setUsername(request.getPayload().getUsername());
        user.setEmail(request.getPayload().getEmail());
        user.setPassword(request.getPayload().getPassword());
        user.setType(type);

        return repository.create(user).thenApplyAsync(_user -> {
            return rg.generateResponse(request, "Success", _user.get(), "ok");
        }, ec.current());
    }

    public CompletionStage<Result> login(Http.Request _request) throws JsonProcessingException {
        JavaType javaType = Json.mapper().getTypeFactory().constructParametricType(RequestResource.class, LoginInfo.class);
        RequestResource<LoginInfo> request = Json.mapper().readValue(_request.body().asJson().toString(), javaType);

        String username = request.getPayload().getUsername();
        String password = request.getPayload().getPassword();

        return repository.checkUserPass(username, password).thenApplyAsync(_user -> {
            //check if login was a success
            if (_user.isPresent()) {
                //if yes add to cache
                activeUsers.activateUser(_user.get());
                //generate and return token
                ObjectNode result = Json.newObject();
                try {
                    result.put("access_token", getSignedToken(_user.get()));
                    result.put("token_type", "Bearer");
                    result.put("expires_in", "43200");
                } catch (UnsupportedEncodingException e) {
                    Logger.error(e.getMessage());
                    return rg.generateResponse(request, "Login Failure", "Error Generating JWT Token", "internalServerError");
                }
                return rg.generateResponse(request, "Login Success", result, "ok");
            } else {
                //if no return error response
                return rg.generateResponse(request, "Login Failure", "Username or Password failure.", "ok");
            }
        }, ec.current());
    }

    public CompletionStage<Result> get(Http.Request _request) throws JsonProcessingException {
        RequestResource request = Json.mapper().readValue(_request.body().asJson().toString(), RequestResource.class);
        String username = request.getUsername();
        return repository.findByName(username).thenApplyAsync(_user -> {
            return rg.generateResponse(request, "Success", _user, "ok");
        }, ec.current());
    }

    private String getSignedToken(User user) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("tableTop")
                .withClaim("username", user.getUsername())
                .withClaim("roles", user.getRoles())
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(720).toInstant()))
                .sign(algorithm);
    }
}
