package dev.shendriks.fitnesstrackerapi.security;

import dev.shendriks.fitnesstrackerapi.application.entity.Application;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class ApiKeyAuthentication implements Authentication {
    private final String apiKey;
    private boolean authenticated = false;
    private Application application;

    public ApiKeyAuthentication(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return application;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public String getName() {
        return application != null ? application.getName() : null;
    }
}
