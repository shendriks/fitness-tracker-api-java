package dev.shendriks.fitnesstrackerapi.developer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public final class DeveloperSignupRequest {
    @NotNull
    @NotEmpty
    @Email
    private final String email;
    
    @NotNull
    @NotEmpty
    private final String password;

    public DeveloperSignupRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DeveloperSignupRequest) obj;
        return Objects.equals(this.email, that.email) &&
                Objects.equals(this.password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "DeveloperSignupRequest[" +
                "email=" + email + ", " +
                "password=" + password + ']';
    }
}
