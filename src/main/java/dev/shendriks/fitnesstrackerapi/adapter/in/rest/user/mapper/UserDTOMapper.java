package dev.shendriks.fitnesstrackerapi.adapter.in.rest.user.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.user.dto.UserResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.user.dto.UserSignupRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.user.dto.UserUpdateRequestDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    @Mapping(target = "email", expression = "java(request.email().toLowerCase().trim())")
    @Mapping(target = "accountType", expression = "java(AccountType.fromString(request.accountType()))")
    UserSignupData toUserSignUpData(UserSignupRequestDTO request);

    @Mapping(target = "email", expression = "java(request.email().toLowerCase().trim())")
    @Mapping(target = "accountType", expression = "java(AccountType.fromString(request.accountType()))")
    UserUpdateData toUserUpdateData(UserUpdateRequestDTO request);

    @Mapping(target = "id", source = "ulid.value")
    UserResponseDTO toUserResponse(User user);
}
