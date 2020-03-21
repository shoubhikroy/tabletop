package interceptors.body;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import handlers.AccountHandler;
import models.RequestResource;
import models.accounts.RegistrationInfo;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import handlers.ResponseHandler;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class AccountsMassager extends Action.Simple {
    static ResponseHandler rg;

    public CompletionStage<Result> call(Http.Request req) {
        Logger.info("1Calling action for {}", req);
        RequestResource<Object> resource = null;
        CompletionStage<Result> call;
        try {
            call = delegate.call(req);
        } catch (Exception e) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();
            Logger.error(sStackTrace);

            resource = new RequestResource<>("not_supplied", req.uri(), null);
            Result rr = rg.generatedErrorResponse(resource, "Looks like empty or bad data", e.getMessage(), "badRequest");
            return CompletableFuture.completedFuture(rr);
        }
        Logger.info("2Calling action for {}", req);
        return call;
    }
}