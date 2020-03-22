package interceptors.headers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;

import akka.stream.Materializer;
import play.Logger;
import play.api.Configuration;
import play.mvc.*;
import play.routing.HandlerDef;
import play.routing.Router;

import static play.mvc.Results.forbidden;

public class ClientKeyFilter extends Filter {
    private static final String ERR_AUTHORIZATION_HEADER = "ERROR: Client Key Invalid - Access is denied";
    private static final String ROUTE_MODIFIER_BYPASS_AUTH = "bypassClientKey";
    Configuration config;

    @Inject
    public ClientKeyFilter(Materializer mat, Configuration config) {
        super(mat);
        this.config = config;
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        if (requestHeader.attrs().containsKey(Router.Attrs.HANDLER_DEF)) {
            HandlerDef handler = requestHeader.attrs().get(Router.Attrs.HANDLER_DEF);
            List<String> modifiers = handler.getModifiers();

            if (modifiers.contains(ROUTE_MODIFIER_BYPASS_AUTH)) {
                Logger.info("byPassing ClientKey Authorization + " + requestHeader.toString());
                return nextFilter.apply(requestHeader);
            }
        }

        Optional<String> authHeader =  requestHeader.getHeaders().get("client-key");

        //TODO: Make this accept a list, and move it to a settings file
        if (!authHeader.filter(ah -> ah.contains("dGFibGV0b3BfY2xpZW50X2tleV9vbmU=")).isPresent()) {
            Logger.error("ERROR: Client Key Not Present - Access is denied");
            return CompletableFuture.completedFuture(forbidden(ERR_AUTHORIZATION_HEADER));
        }

        return nextFilter.apply(requestHeader);
    }
}