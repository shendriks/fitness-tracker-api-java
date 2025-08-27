package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class UserJpaRepositoryAdapterIntegrationTest {
    private final UserJpaRepositoryAdapter adapter;
    private final PasswordEncoder passwordEncoder;
    private final TestEntityManager entityManager;

    public UserJpaRepositoryAdapterIntegrationTest(
        @Autowired UserJpaRepositoryAdapter adapter,
        @Autowired PasswordEncoder passwordEncoder,
        @Autowired TestEntityManager entityManager
    ) {
        this.adapter = adapter;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    @Test
    void createUserThenFindThenExists_worksAsExpected() {
        String email = "it.user.create@example.com";

        assertFalse(adapter.existsByEmail(email), "Expected user to not exist before create");

        UserSignupData signup = UserSignupData
            .builder()
            .name("Jane Doe")
            .email(email)
            .password("plain-password")
            .accountType(AccountType.BASIC)
            .build();

        User actualCreatedUser = adapter.createUser(signup);

        assertInstanceOf(User.class, actualCreatedUser);
        assertInstanceOf(UserId.class, actualCreatedUser.id());
        assertInstanceOf(UserUlid.class, actualCreatedUser.ulid());
        assertEquals("Jane Doe", actualCreatedUser.name());
        assertEquals(email, actualCreatedUser.email());
        assertEquals(AccountType.BASIC, actualCreatedUser.accountType());
        assertNotNull(actualCreatedUser.password());
        assertTrue(passwordEncoder.matches("plain-password", actualCreatedUser.password()), "Expected stored password to be encoded and match");
        assertEquals("ROLE_USER", actualCreatedUser.authority());
        assertNotNull(actualCreatedUser.createdAt());
        assertNotNull(actualCreatedUser.updatedAt());

        assertTrue(adapter.existsByEmail(email), "Expected user to be found by email");

        Optional<User> byId = adapter.findById(actualCreatedUser.id());
        Optional<User> byEmail = adapter.findByEmail(email);
        Optional<User> byUlid = adapter.findByUlid(actualCreatedUser.ulid());

        assertTrue(byId.isPresent());
        assertTrue(byEmail.isPresent());
        assertTrue(byUlid.isPresent());
        assertEquals(actualCreatedUser.id(), byId.get().id());
        assertEquals(actualCreatedUser.id(), byEmail.get().id());
        assertEquals(actualCreatedUser.id(), byUlid.get().id());

        UserDbEntity userDbEntity = entityManager.find(UserDbEntity.class, actualCreatedUser.id().value());
        assertInstanceOf(UserDbEntity.class, userDbEntity);
        assertEquals(email, userDbEntity.getEmail());
        assertEquals("Jane Doe", userDbEntity.getName());
        assertEquals(AccountType.BASIC, userDbEntity.getAccountType());
        assertTrue(passwordEncoder.matches("plain-password", userDbEntity.getPassword()));
        assertEquals("ROLE_USER", userDbEntity.getAuthority());
        assertNotNull(userDbEntity.getUlid());
        assertNotNull(userDbEntity.getCreatedAt());
        assertNotNull(userDbEntity.getUpdatedAt());
    }

    @Test
    void updateUser_updatesFieldsAndReencodesPassword() {
        String email = "it.user.update@example.com";
        UserSignupData signup = UserSignupData
            .builder()
            .name("John")
            .email(email)
            .password("old-pass")
            .accountType(AccountType.BASIC)
            .build();
        User created = adapter.createUser(signup);

        String newEmail = "it.user.update.new@example.com";
        UserUpdateData update = UserUpdateData
            .builder()
            .name("John Updated")
            .email(newEmail)
            .newPassword("new-secret")
            .currentPassword("ignoredByAdapterHere")
            .accountType(AccountType.PREMIUM)
            .build();

        User actualUpdatedUser = adapter.updateUser(created.id(), update);

        assertEquals(created.id(), actualUpdatedUser.id());
        assertEquals(newEmail, actualUpdatedUser.email());
        assertEquals("John Updated", actualUpdatedUser.name());
        assertEquals(AccountType.PREMIUM, actualUpdatedUser.accountType());
        assertTrue(passwordEncoder.matches("new-secret", actualUpdatedUser.password()));
        assertEquals("ROLE_USER", actualUpdatedUser.authority());

        UserDbEntity dbEntity = entityManager.find(UserDbEntity.class, created.id().value());
        assertInstanceOf(UserDbEntity.class, dbEntity);
        assertEquals(newEmail, dbEntity.getEmail());
        assertEquals("John Updated", dbEntity.getName());
        assertEquals(AccountType.PREMIUM, dbEntity.getAccountType());
        assertTrue(passwordEncoder.matches("new-secret", dbEntity.getPassword()));
        assertTrue(dbEntity.getUpdatedAt().isAfter(dbEntity.getCreatedAt()) || dbEntity.getUpdatedAt().equals(dbEntity.getCreatedAt()));

        Optional<User> byId = adapter.findById(created.id());
        Optional<User> byEmail = adapter.findByEmail(newEmail);
        Optional<User> byUlid = adapter.findByUlid(actualUpdatedUser.ulid());
        assertTrue(byId.isPresent());
        assertTrue(byEmail.isPresent());
        assertTrue(byUlid.isPresent());
        assertEquals(actualUpdatedUser.id(), byEmail.get().id());
    }
}
