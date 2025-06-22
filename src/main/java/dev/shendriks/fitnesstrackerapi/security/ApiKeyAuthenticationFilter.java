package dev.shendriks.fitnesstrackerapi.security;

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
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    public static final String HEADER_API_KEY = "X-API-Key";

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    
    // todo fix this ("/api/activities")
    private final RequestMatcher matcher = PathPatternRequestMatcher.withDefaults().matcher("/api/activities");

    private final AuthenticationEntryPoint authenticationEntryPoint = (request, response, ex) -> {
        resolver.resolveException(request, response, null, ex);
    };

    private final ApiKeyAuthenticationProvider provider;

    public ApiKeyAuthenticationFilter(ApiKeyAuthenticationProvider provider) {
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
            var apiKey = request.getHeader(HEADER_API_KEY);
            if (apiKey == null) {
                throw new BadCredentialsException("API Key is required");
            }
            
            Authentication authentication = new ApiKeyAuthentication(apiKey);
            authentication = provider.authenticate(authentication);
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
        }
    }
}