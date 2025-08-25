package dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user.UserResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user.UserSignupRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user.UserUpdateRequestDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOMapperTest {
    private final UserDTOMapper mapper = Mappers.getMapper(UserDTOMapper.class);

    private static User buildUser(Instant createdAt, Instant updatedAt) {
        return User
            .builder()
            .id(new UserId(1L))
            .ulid(new UserUlid("TESTULID000000000000000001"))
            .name("Charlie")
            .email("charlie@example.com")
            .password("hashed-password")
            .authority("ROLE_USER")
            .accountType(AccountType.PREMIUM)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }

    @Test
    void toUserSignUpData_shouldNormalizeEmailAndMapAccountType() {
        UserSignupRequestDTO request = new UserSignupRequestDTO(
            "Alice",
            "  ALICE@example.COM  ",
            "Secret123!",
            "PrEmIuM"
        );

        UserSignupData data = mapper.toUserSignUpData(request);

        assertNotNull(data);
        assertEquals("Alice", data.name());
        assertEquals("alice@example.com", data.email());
        assertEquals("Secret123!", data.password());
        assertEquals(AccountType.PREMIUM, data.accountType());
    }

    @Test
    void toUserUpdateData_shouldNormalizeEmailAndMapPasswordFieldsAndAccountType() {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO(
            "Bob",
            "  Bob@Example.com ",
            "NewPass!",
            "OldPass!",
            "basic"
        );

        UserUpdateData data = mapper.toUserUpdateData(request);

        assertNotNull(data);
        assertEquals("Bob", data.name());
        assertEquals("bob@example.com", data.email());
        assertEquals("NewPass!", data.newPassword());
        assertEquals("OldPass!", data.currentPassword());
        assertEquals(AccountType.BASIC, data.accountType());
    }

    @Test
    void toUserResponse_shouldMapAllFields_idFromUlid() {
        Instant createdAt = Instant.parse("2025-08-20T10:00:00Z");
        Instant updatedAt = Instant.parse("2025-08-21T10:00:00Z");
        User user = buildUser(createdAt, updatedAt);

        UserResponseDTO dto = mapper.toUserResponse(user);

        assertNotNull(dto);
        assertEquals("TESTULID000000000000000001", dto.id());
        assertEquals("Charlie", dto.name());
        assertEquals("charlie@example.com", dto.email());
        assertEquals(AccountType.PREMIUM, dto.accountType());
        assertEquals(createdAt, dto.createdAt());
        assertEquals(updatedAt, dto.updatedAt());
    }

    @Test
    void toUserSignUpData_withNullInput_returnsNull() {
        assertNull(mapper.toUserSignUpData(null));
    }

    @Test
    void toUserUpdateData_withNullInput_returnsNull() {
        assertNull(mapper.toUserUpdateData(null));
    }

    @Test
    void toUserResponse_withNullInput_returnsNull() {
        assertNull(mapper.toUserResponse(null));
    }
}
