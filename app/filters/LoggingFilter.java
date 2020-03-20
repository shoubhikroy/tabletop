package filters;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import javax.inject.Inject;
import akka.stream.Materializer;

import play.Logger;
import play.mvc.*;

public class LoggingFilter extends Filter {

    @Inject
    public LoggingFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(
            Function<Http.RequestHeader, CompletionStage<Result>> nextFilter,
            Http.RequestHeader requestHeader) {
        long startTime = System.currentTimeMillis();

        Map<String, List<String>> headerMap = requestHeader.getHeaders().asMap();
        for (Map.Entry<String, List<String>> header : headerMap.entrySet()) {
            for (String headerValue : header.getValue()) {
                Logger.info(String.format("%-20s= %s" , header.getKey(), headerValue));
            }
        }

        return nextFilter
                .apply(requestHeader)
                .thenApply(
                        result -> {
                            long endTime = System.currentTimeMillis();
                            long requestTime = endTime - startTime;

                            Logger.info(
                                    "{} {} took {}ms and returned {}",
                                    requestHeader.method(),
                                    requestHeader.uri(),
                                    requestTime,
                                    result.status());

                            return result.withHeader("Request-Time", "" + requestTime);
                        });
    }
}