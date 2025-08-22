package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import lombok.Builder;

import java.time.Instant;

@Builder
public record User(
    UserId id,
    UserUlid ulid,
    String name,
    String email,
    String password,
    String authority,
    Instant createdAt,
    Instant updatedAt,
    AccountType accountType
) {
}
