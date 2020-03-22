
import interceptors.headers.HeaderFirewall;
import interceptors.headers.ClientKeyFilter;
import interceptors.headers.LoggingFilter;
import jwt.filter.JwtFilter;
import play.Environment;
import play.api.http.EnabledFilters;
import play.filters.cors.CORSFilter;
import play.http.DefaultHttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Filters extends DefaultHttpFilters {
    private final Environment env;
    private final EssentialFilter jwtFilter;
    private final EnabledFilters enabledFilters;
    private final CORSFilter corsFilter;
    private final EssentialFilter logFilter;
    private final EssentialFilter clientKeyFilter;
    private final EssentialFilter headerFirewall;

    @Inject
    public Filters(Environment env, JwtFilter jwtFilter,
                   EnabledFilters enabledFilters, CORSFilter corsFilter,
                   LoggingFilter logFilter, HeaderFirewall headerFirewall,
                   ClientKeyFilter clientKeyFilter) {
        this.env = env;
        this.jwtFilter = jwtFilter;
        this.enabledFilters = enabledFilters;
        this.corsFilter = corsFilter;
        this.logFilter = logFilter;
        this.clientKeyFilter = clientKeyFilter;
        this.headerFirewall = headerFirewall;
    }

    private static List<EssentialFilter> combine(List<EssentialFilter> filters, EssentialFilter toAppend) {
        List<EssentialFilter> combinedFilters = new ArrayList<>(filters);
        combinedFilters.add(toAppend);
        return combinedFilters;
    }

    @Override
    public List<EssentialFilter> getFilters() {
        List<EssentialFilter> zeFilters = enabledFilters.asJava().getFilters();
        zeFilters.add(corsFilter.asJava());
        zeFilters.add(logFilter);
        zeFilters.add(jwtFilter);
        zeFilters.add(clientKeyFilter);
        zeFilters.add(headerFirewall);
        return zeFilters;
    }
}