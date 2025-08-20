package dev.shendriks.fitnesstrackerapi.application.port.in.user;

import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;

public interface SignUpUseCase {
    User signUp(UserSignupData request);
}
