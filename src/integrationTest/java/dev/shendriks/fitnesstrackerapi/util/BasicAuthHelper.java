package dev.shendriks.fitnesstrackerapi.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthHelper {
    public String createBasicAuthHeader(String email, String password) {
        String credentials = email + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}