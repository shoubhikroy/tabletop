
package interceptors.headers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import javax.inject.Inject;
import akka.stream.Materializer;
import cache.ActiveUsers;
import dbobjects.user.UserRepository;
import jwt.Attrs;
import jwt.JwtValidator;
import jwt.VerifiedJwt;
import play.Logger;
import play.libs.F;
import play.mvc.*;
import play.routing.HandlerDef;
import play.routing.Router;

import static play.mvc.Results.forbidden;

public class JwtFilter extends Filter {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String ROUTE_MODIFIER_BYPASS_AUTH = "bypassAuth";
    private static final String ERR_AUTHORIZATION_HEADER = "ERR_AUTHORIZATION_HEADER";
    private JwtValidator jwtValidator;
    private final ActiveUsers activeUsers;
    private final UserRepository userRepository;

    @Inject
    public JwtFilter(Materializer mat, JwtValidator jwtValidator, ActiveUsers activeUsers, UserRepository userRepository) {
        super(mat);
        this.jwtValidator = jwtValidator;
        this.activeUsers = activeUsers;
        this.userRepository = userRepository;
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        if (requestHeader.attrs().containsKey(Router.Attrs.HANDLER_DEF)) {
            HandlerDef handler = requestHeader.attrs().get(Router.Attrs.HANDLER_DEF);
            List<String> modifiers = handler.getModifiers();

            if (modifiers.contains(ROUTE_MODIFIER_BYPASS_AUTH)) {
                Logger.info("byPassing JWT Authorization + " + requestHeader.toString());
                return nextFilter.apply(requestHeader);
            }
        }

        Optional<String> authHeader =  requestHeader.getHeaders().get(HEADER_AUTHORIZATION);

        if (!authHeader.filter(ah -> ah.contains(BEARER)).isPresent()) {
            Logger.error("ERROR: Auth Header Not Present - Access is denied");
            return CompletableFuture.completedFuture(forbidden(ERR_AUTHORIZATION_HEADER));
        }

        String token = authHeader.map(ah -> ah.replace(BEARER, "")).orElse("");
        F.Either<JwtValidator.Error, VerifiedJwt> res = jwtValidator.verify(token);

        if (res.left.isPresent()) {
            Logger.error("ERROR: Auth Header Verification Failed - Access is denied");
            return CompletableFuture.completedFuture(forbidden(res.left.get().toString()));
        }

        Logger.info("JWT Authorization Succeeded: " + res.right.get());
        activeUsers.activate(res.right.get().getUserId()).toCompletableFuture();
        return nextFilter.apply(requestHeader.withAttrs(requestHeader.attrs().put(Attrs.VERIFIED_JWT, res.right.get())));
    }
}