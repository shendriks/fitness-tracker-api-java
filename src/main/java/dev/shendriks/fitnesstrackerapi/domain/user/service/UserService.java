package dev.shendriks.fitnesstrackerapi.domain.user.service;

import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserResponse;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserSignupRequest;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserUpdateRequest;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.event.UserSignedUpEvent;
import dev.shendriks.fitnesstrackerapi.domain.user.event.UserUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.domain.user.exception.CurrentPasswordDoesntMatchException;
import dev.shendriks.fitnesstrackerapi.domain.user.exception.EmailAlreadyRegisteredException;
import dev.shendriks.fitnesstrackerapi.domain.user.mapper.UserMapper;
import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository repository,
        UserMapper mapper,
        ApplicationEventPublisher eventPublisher,
        PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse signUp(UserSignupRequest request) {
        String email = request.email().toLowerCase().trim();
        UserResponse existingUser = findUserByEmail(email);
        if (existingUser != null) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = mapper.toEntity(request);
        repository.save(user);

        eventPublisher.publishEvent(new UserSignedUpEvent(this, user.getId()));

        return mapper.toResponse(user);
    }

    public UserResponse findUserById(String id) {
        var user = repository.findByUlid(id).orElse(null);
        if (user == null) {
            return null;
        }

        return mapper.toResponse(user);
    }

    public UserResponse findUserByEmail(String email) {
        var user = repository.findByEmail(email).orElse(null);
        if (user == null) {
            return null;
        }

        return mapper.toResponse(user);
    }

    public void updateUser(User user, UserUpdateRequest request) {
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new CurrentPasswordDoesntMatchException();
        }

        String newEmail = request.email().toLowerCase().trim();
        repository.findByEmailAndIdNot(newEmail, user.getId()).ifPresent(existingUser -> {
            throw new EmailAlreadyRegisteredException();
        });

        mapper.updateEntity(user, request);
        repository.save(user);
        eventPublisher.publishEvent(new UserUpdatedEvent(this, user.getId()));
    }
}
