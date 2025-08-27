package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserDbEntityMapperTest {
    private final UserDbEntityMapper mapper;

    {
        UserDbEntityMapper mapper1 = Mappers.getMapper(UserDbEntityMapper.class);
        Class<?> clazz = mapper1.getClass();
        Field passwordEncoderField;
        try {
            passwordEncoderField = clazz.getSuperclass().getDeclaredField("passwordEncoder");
            passwordEncoderField.setAccessible(true);
            passwordEncoderField.set(mapper1, new StubPasswordEncoder());
            mapper = mapper1;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void toUserDbEntity_encodesPasswordAndSetsAuthorityAndCopiesFields() {
        UserSignupData signup = UserSignupData
            .builder()
            .name("Alice")
            .email("alice@example.com")
            .password("Secret123!")
            .accountType(AccountType.PREMIUM)
            .build();

        UserDbEntity entity = mapper.toUserDbEntity(signup);

        assertNotNull(entity);
        assertEquals("Alice", entity.getName());
        assertEquals("alice@example.com", entity.getEmail());
        assertEquals("hashed:Secret123!", entity.getPassword());
        assertEquals("ROLE_USER", entity.getAuthority());
        assertEquals(AccountType.PREMIUM, entity.getAccountType());
        assertNull(entity.getId());
        assertNull(entity.getUlid());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
        assertNull(entity.getTrophies());
        assertNull(entity.getChallengeParticipations());
    }

    @Test
    void toUser_mapsAllFieldsAndWrapIds() {
        Instant createdAt = Instant.parse("2025-08-20T10:00:00Z");
        Instant updatedAt = Instant.parse("2025-08-21T11:00:00Z");

        UserDbEntity entity = UserDbEntity
            .builder()
            .id(7L)
            .ulid("TESTULID000000000000000007")
            .name("Bob")
            .email("bob@example.com")
            .password("hashed:pw")
            .authority("ROLE_ADMIN")
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .accountType(AccountType.BASIC)
            .build();

        User user = mapper.toUser(entity);

        assertNotNull(user);
        assertEquals(new UserId(7L), user.id());
        assertEquals(new UserUlid("TESTULID000000000000000007"), user.ulid());
        assertEquals("Bob", user.name());
        assertEquals("bob@example.com", user.email());
        assertEquals("hashed:pw", user.password());
        assertEquals("ROLE_ADMIN", user.authority());
        assertEquals(createdAt, user.createdAt());
        assertEquals(updatedAt, user.updatedAt());
        assertEquals(AccountType.BASIC, user.accountType());
    }

    @Test
    void toUserDbEntity_withNullInput_returnsNull() {
        assertNull(mapper.toUserDbEntity(null));
    }

    @Test
    void toUser_withNullInput_returnsNull() {
        assertNull(mapper.toUser(null));
    }

    private static class StubPasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return "hashed:" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return ("hashed:" + rawPassword).contentEquals(encodedPassword);
        }
    }
}
