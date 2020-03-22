package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import dbobjects.user.UserRepository;
import handlers.AccountHandler;
import handlers.ResourceHandler;
import jwt.JwtControllerHelper;
import jwt.VerifiedJwt;
import jwt.Attrs;
import play.Logger;
import play.data.FormFactory;
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

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class Landing extends Controller {
    private final HttpExecutionContext ec;

    static ResourceHandler rg;

    @Inject
    public Landing(HttpExecutionContext ec) {
        this.ec = ec;
    }
    public CompletionStage<Result> healthCheck(Request request) {
        Logger.info(request.toString());

        return supplyAsync(() -> {
            return ok("Server is Up: " + request.toString());
        }, ec.current());
    }
}
