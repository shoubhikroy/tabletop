package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import dbobjects.user.UserRepository;
import jwt.JwtControllerHelper;
import jwt.VerifiedJwt;
import jwt.filter.Attrs;
import play.Logger;
import play.cache.NamedCache;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import play.cache.redis.AsyncCacheApi;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class Landing extends Controller {
    @Inject
    private JwtControllerHelper jwtControllerHelper;

    @Inject
    private Config config;

//    @Inject
//    @NamedCache("games")
//    private AsyncCacheApi gameCache;
//
//    @Inject
//    @NamedCache("local")
//    private AsyncCacheApi localCache;

    @Inject
    private HttpExecutionContext ec;

    @Inject
    private UserRepository userRepository;

    public CompletionStage<Result> healthCheck(Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok("Server is Up: " + request.toString());
        }, ec.current());
//        Logger.info("|game: " + game +
//                    " key: " + key +
//                    " local: " + local);
//
//        long startTime = System.currentTimeMillis();
//
//        CompletionStage<Optional<String>> i = gameCache.get(key);
//        CompletionStage<Optional<String>> j = localCache.get(key);
//
//        Optional<String> localValue= j.toCompletableFuture().get();
//        Optional<String> gameValue= i.toCompletableFuture().get();
//
//
//        if (gameValue.isPresent() && localValue.isPresent()) {
//            long endTime = System.currentTimeMillis();
//            Logger.info("gameCacheValue: " + gameValue.get());
//            Logger.info("localCacheValue: " + localValue.get());
//            return ok(gameValue.get() + "|" + localValue.get() + ": " + (endTime - startTime));
//        } else {
//            Logger.info("value not present logging");
//            gameCache.set(key, game);
//            localCache.set(key, local);
//        }
//
//        long endTime = System.currentTimeMillis();
//        return ok("noCache: " + (endTime - startTime));
    }

    public Result generateSignedToken() throws UnsupportedEncodingException {
        return ok("signed token: " + getSignedToken(5l));
    }

    public Result login(Http.Request request) throws UnsupportedEncodingException {
        JsonNode body = request.body().asJson();

        if (body == null) {
            Logger.error("json body is null");
            return forbidden();
        }

        if (body.hasNonNull("username") && body.hasNonNull("password") && body.get("username").asText().equals("abc")) {
            ObjectNode result = Json.newObject();
            result.put("access_token", getSignedToken(7l));
            return ok(result);
        } else {
            Logger.error("json body not as expected: {}", body.toString());
        }

        return forbidden();
    }

    private String getSignedToken(Long userId) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("tableTop")
                .withClaim("user_id", userId)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(10).toInstant()))
                .sign(algorithm);
    }

    public Result requiresJwt(Http.Request request) {
        return jwtControllerHelper.verify(request, res -> {
            if (res.left.isPresent()) {
                return forbidden(res.left.get().toString());
            }

            VerifiedJwt verifiedJwt = res.right.get();
            Logger.debug("{}", verifiedJwt);

            ObjectNode result = Json.newObject();
            result.put("access", "granted");
            result.put("secret_data", "birds fly");
            return ok(result);
        });
    }

    public Result requiresJwtViaFilter(Http.Request request) {
        Optional<VerifiedJwt> oVerifiedJwt = request.attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(jwt -> {
            Logger.debug(jwt.toString());
            return ok("access granted via filter");
        }).orElse(forbidden("eh, no verified jwt found"));
    }
}
