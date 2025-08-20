package dev.shendriks.fitnesstrackerapi.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String HEADER_ACCESS_TOKEN = "Authorization";
    // todo fix hard-coded paths
    private final RequestMatcher matcher = new OrRequestMatcher(
        PathPatternRequestMatcher.withDefaults().matcher("/api/activities/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/challenges/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/challenge-participations"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/trophies/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/milestones/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/notifications/**"),
        PathPatternRequestMatcher.withDefaults().matcher("/api/users/me")
    );
    private final JwtAuthenticationProvider provider;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    private final AuthenticationEntryPoint authenticationEntryPoint = (request, response, ex) -> resolver.resolveException(request, response, null, ex);

    public JwtAuthenticationFilter(JwtAuthenticationProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (!matcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var authorizationHeader = request.getHeader(HEADER_ACCESS_TOKEN);
            var jwt = authorizationHeader != null ? authorizationHeader.replace("Bearer ", "") : null;
            if (jwt == null) {
                throw new BadCredentialsException("Access token is required");
            }

            Authentication authentication = new JwtAuthentication(jwt);
            authentication = provider.authenticate(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
        }
    }
}