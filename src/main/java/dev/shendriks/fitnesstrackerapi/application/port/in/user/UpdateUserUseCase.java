package dev.shendriks.fitnesstrackerapi.application.port.in.user;

import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;

public interface UpdateUserUseCase {
    User updateUser(UserId userId, UserUpdateData userUpdateData);
}
