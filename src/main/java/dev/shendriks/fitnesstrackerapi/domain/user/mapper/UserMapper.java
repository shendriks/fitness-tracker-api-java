package dev.shendriks.fitnesstrackerapi.domain.user.mapper;

import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserResponse;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserSignupRequest;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getUlid(), 
            user.getEmail(),
            user.getAccountType()
        );
    }

    public User toEntity(UserSignupRequest request) {
        User user = new User();
        user.setEmail(request.email().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAuthority("ROLE_USER");
        user.setAccountType(AccountType.fromString(request.accountType()));
        return user;
    }
}
