package interceptors.headers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;

import akka.stream.Materializer;
import play.Logger;
import play.api.Configuration;
import play.mvc.*;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.forbidden;

public class HeaderFirewall extends Filter {
    private static final String ERR_AUTHORIZATION_HEADER = "ERROR: Wrong content-type, required application/json.";
    Configuration config;

    @Inject
    public HeaderFirewall(Materializer mat, Configuration config) {
        super(mat);
        this.config = config;
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        Optional<String> authHeader =  requestHeader.getHeaders().get("Content-Type");

        if (!authHeader.filter(ah -> ah.contains("application/json")).isPresent()) {
            Logger.error("ERROR: Wrong content-type");
            return CompletableFuture.completedFuture(badRequest(ERR_AUTHORIZATION_HEADER));
        }

        return nextFilter.apply(requestHeader);
    }
}