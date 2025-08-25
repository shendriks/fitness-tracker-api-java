package dev.shendriks.fitnesstrackerapi.application.service;

import dev.shendriks.fitnesstrackerapi.application.event.UserSignedUpEvent;
import dev.shendriks.fitnesstrackerapi.application.event.UserUpdatedEvent;
import dev.shendriks.fitnesstrackerapi.application.exception.CurrentPasswordDoesntMatchException;
import dev.shendriks.fitnesstrackerapi.application.exception.EmailAlreadyRegisteredException;
import dev.shendriks.fitnesstrackerapi.application.exception.UserNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingUsers;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private ForAccessingUsers forAccessingUsers;
    private ApplicationEventPublisher eventPublisher;
    private PasswordEncoder passwordEncoder;
    private UserService service;

    private static User buildUser(UserId id, String email, String password) {
        return User
            .builder()
            .id(id)
            .ulid(new UserUlid("TESTULID000000000000000001"))
            .name("John Doe")
            .email(email)
            .password(password)
            .authority("ROLE_USER")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .accountType(AccountType.BASIC)
            .build();
    }

    @BeforeEach
    void setUp() {
        forAccessingUsers = mock(ForAccessingUsers.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserService(forAccessingUsers, eventPublisher, passwordEncoder);
    }

    @Test
    void signUp_whenEmailAlreadyExists_throwsEmailAlreadyRegisteredException() {
        UserSignupData signup = UserSignupData
            .builder()
            .name("Jane")
            .email("jane@example.com")
            .password("plain")
            .accountType(AccountType.BASIC)
            .build();

        when(forAccessingUsers.existsByEmail(signup.email())).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class, () -> service.signUp(signup));

        verify(forAccessingUsers).existsByEmail(signup.email());
        verify(forAccessingUsers, never()).createUser(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void signUp_whenEmailNotExists_createsUser_andPublishesEvent_andReturnsUser() {
        UserSignupData signup = UserSignupData
            .builder()
            .name("Jane Doe")
            .email("jane@example.com")
            .password("plain")
            .accountType(AccountType.BASIC)
            .build();

        when(forAccessingUsers.existsByEmail(signup.email())).thenReturn(false);
        User user = buildUser(new UserId(1L), signup.email(), "encoded");
        when(forAccessingUsers.createUser(signup)).thenReturn(user);

        User actualUser = service.signUp(signup);

        assertEquals(user, actualUser);
        verify(forAccessingUsers).createUser(signup);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(UserSignedUpEvent.class, event);
        UserSignedUpEvent userSignedUpEvent = (UserSignedUpEvent) event;
        assertEquals(user.id(), userSignedUpEvent.userId());
    }

    @Test
    void updateUser_whenUserNotFound_throwsUserNotFoundException() {
        UserId userId = new UserId(42L);
        when(forAccessingUsers.findById(userId)).thenReturn(Optional.empty());

        UserUpdateData updateData = UserUpdateData
            .builder()
            .name("Jane")
            .email("jane@example.com")
            .newPassword("new")
            .currentPassword("curr")
            .accountType(AccountType.BASIC)
            .build();

        assertThrows(UserNotFoundException.class, () -> service.updateUser(userId, updateData));

        verify(forAccessingUsers).findById(userId);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void updateUser_whenCurrentPasswordDoesntMatch_throws() {
        UserId userId = new UserId(42L);
        User existing = buildUser(userId, "john@example.com", "encodedCurr");
        when(forAccessingUsers.findById(userId)).thenReturn(Optional.of(existing));

        UserUpdateData updateData = UserUpdateData
            .builder()
            .name("John")
            .email(existing.email())
            .newPassword("newPass")
            .currentPassword("wrongCurr")
            .accountType(AccountType.BASIC)
            .build();
        when(passwordEncoder.matches(updateData.currentPassword(), existing.password())).thenReturn(false);

        assertThrows(CurrentPasswordDoesntMatchException.class, () -> service.updateUser(userId, updateData));

        verify(passwordEncoder).matches(updateData.currentPassword(), existing.password());
        verify(forAccessingUsers, never()).updateUser(any(), any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void updateUser_whenEmailChangedAndAlreadyExists_throws() {
        UserId userId = new UserId(42L);
        User existing = buildUser(userId, "john@example.com", "encodedCurr");
        when(forAccessingUsers.findById(userId)).thenReturn(Optional.of(existing));

        UserUpdateData updateData = UserUpdateData
            .builder()
            .name("John")
            .email("used@example.com")
            .newPassword("newPass")
            .currentPassword("curr")
            .accountType(AccountType.BASIC)
            .build();
        when(passwordEncoder.matches(updateData.currentPassword(), existing.password())).thenReturn(true);
        when(forAccessingUsers.existsByEmail(updateData.email())).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class, () -> service.updateUser(userId, updateData));

        verify(forAccessingUsers).existsByEmail(updateData.email());
        verify(forAccessingUsers, never()).updateUser(any(), any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void updateUser_happyPath_updatesUser_andPublishesEvent_andReturnsUpdated() {
        UserId userId = new UserId(42L);
        User existing = buildUser(userId, "john@example.com", "encodedCurr");
        when(forAccessingUsers.findById(userId)).thenReturn(Optional.of(existing));

        UserUpdateData updateData = UserUpdateData
            .builder()
            .name("John D.")
            .email("john.new@example.com")
            .newPassword("newPass")
            .currentPassword("curr")
            .accountType(AccountType.BASIC)
            .build();
        when(passwordEncoder.matches(updateData.currentPassword(), existing.password())).thenReturn(true);
        when(forAccessingUsers.existsByEmail(updateData.email())).thenReturn(false);

        User user = buildUser(userId, updateData.email(), "encodedNew");
        when(forAccessingUsers.updateUser(userId, updateData)).thenReturn(user);

        User actualUser = service.updateUser(userId, updateData);

        assertEquals(user, actualUser);
        verify(forAccessingUsers).updateUser(userId, updateData);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertInstanceOf(UserUpdatedEvent.class, event);
        UserUpdatedEvent updatedEvent = (UserUpdatedEvent) event;
        assertEquals(userId, updatedEvent.userId());
    }
}
