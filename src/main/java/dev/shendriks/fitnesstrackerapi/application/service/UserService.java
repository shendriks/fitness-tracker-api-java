package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.UserSignedUpEvent;
import dev.shendriks.fitnesstrackerapi.application.event.UserUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.CurrentPasswordDoesntMatchException;
import dev.shendriks.fitnesstrackerapi.application.exception.EmailAlreadyRegisteredException;
import dev.shendriks.fitnesstrackerapi.application.exception.UserNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.in.user.SignUpUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.in.user.UpdateUserUseCase;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingUsers;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Application service for user registration and profile updates.
 *
 * <p>Handles validation (email uniqueness, password checks), delegates persistence
 * to ForAccessingUsers, and publishes user-related events.</p>
 */
@Service
@AllArgsConstructor
public class UserService implements SignUpUseCase, UpdateUserUseCase {
    private final ForAccessingUsers forAccessingUsers;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user account and publishes a UserSignedUpEvent.
     *
     * @param userSignupData the signup payload
     * @return the created user
     * @throws EmailAlreadyRegisteredException if the email is already in use
     */
    @Override
    public User signUp(UserSignupData userSignupData) {
        if (forAccessingUsers.existsByEmail(userSignupData.email())) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = forAccessingUsers.createUser(userSignupData);
        eventPublisher.publishEvent(new UserSignedUpEvent(user.id()));
        return user;
    }

    /**
     * Updates a user's profile after validating current password and email uniqueness.
     * Publishes a UserUpdatedEvent on success.
     *
     * @param userId the user identifier
     * @param userUpdateData the changes to apply
     * @return the updated user
     * @throws UserNotFoundException if user does not exist
     * @throws CurrentPasswordDoesntMatchException if the supplied current password is wrong
     * @throws EmailAlreadyRegisteredException if the new email is already taken
     */
    @Override
    public User updateUser(UserId userId, UserUpdateData userUpdateData) {
        User user = forAccessingUsers.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(userUpdateData.currentPassword(), user.password())) {
            throw new CurrentPasswordDoesntMatchException();
        }

        if (!Objects.equals(user.email(), userUpdateData.email())
            && forAccessingUsers.existsByEmail(userUpdateData.email())
        ) {
            throw new EmailAlreadyRegisteredException();
        }

        user = forAccessingUsers.updateUser(userId, userUpdateData);
        eventPublisher.publishEvent(new UserUpdatedEvent(userId));

        return user;
    }
}
