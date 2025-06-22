package dev.shendriks.fitnesstrackerapi.security;

import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.domain.application.repository.ApplicationRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    private final ApplicationRepository repository;

    public ApiKeyAuthenticationProvider(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKey = authentication.getCredentials().toString();

        Application application = repository
                .findByApiKey(apiKey)
                .orElseThrow(() -> new BadCredentialsException("Invalid API Key"));

        ApiKeyAuthentication apiKeyAuthentication = new ApiKeyAuthentication(apiKey);
        apiKeyAuthentication.setApplication(application);
        apiKeyAuthentication.setAuthenticated(true);
        return apiKeyAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthentication.class.equals(authentication);
    }
}