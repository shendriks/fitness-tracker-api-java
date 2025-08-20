package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserDbEntityMapper {
    @Autowired // MapStruct doesn't support constructor injection
    PasswordEncoder passwordEncoder;

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(userSignupData.password()))")
    @Mapping(target = "authority", constant = "ROLE_USER")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ulid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "trophies", ignore = true)
    @Mapping(target = "challengeParticipations", ignore = true)
    public abstract UserDbEntity toUserDbEntity(UserSignupData userSignupData);

    public abstract User toUser(UserDbEntity entity);

    UserId mapUserId(Long id) {
        return new UserId(id);
    }

    UserUlid mapUserUlid(String ulid) {
        return new UserUlid(ulid);
    }
}
