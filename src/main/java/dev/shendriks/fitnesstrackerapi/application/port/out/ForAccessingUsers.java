package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;

import java.util.Optional;

public interface ForAccessingUsers {
    User createUser(UserSignupData userSignupData);

    boolean existsByEmail(String email);

    User updateUser(UserId userId, UserUpdateData userUpdateData);

    Optional<User> findById(UserId userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUlid(UserUlid userId);
}
