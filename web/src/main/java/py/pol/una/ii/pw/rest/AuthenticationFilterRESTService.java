package py.pol.una.ii.pw.rest;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cristhianjbd on 28/04/17.
 */
public class AuthenticationFilterRESTService implements Filter {
    public static final String AUTHENTICATION_HEADER = "X-sessionid";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filter) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            String path = ((HttpServletRequest) request).getPathInfo();
            if ((path != null) && (!path.startsWith("/login"))) {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                String authCredentials = httpServletRequest
                        .getHeader(AUTHENTICATION_HEADER);

                // better injected
                LoginRESTService authenticationService = new LoginRESTService();

                boolean authenticationStatus = authenticationService
                        .authenticate(authCredentials);

                if (authenticationStatus) {
                    filter.doFilter(request, response);
                } else {
                    if (response instanceof HttpServletResponse) {
                        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                        httpServletResponse
                                .setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                }
            }else
                filter.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}
