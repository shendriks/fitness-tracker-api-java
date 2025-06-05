package dev.shendriks.fitnesstrackerapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_API_KEY = "X-API-Key";

    private final RequestMatcher matcher = PathPatternRequestMatcher.withDefaults().matcher("/api/tracker");

    private final AuthenticationEntryPoint authenticationEntryPoint = (request, response, ex) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(ex.getMessage());
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