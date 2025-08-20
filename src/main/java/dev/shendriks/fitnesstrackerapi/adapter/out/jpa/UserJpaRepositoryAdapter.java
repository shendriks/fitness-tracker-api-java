package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.mapper.UserDbEntityMapper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.application.exception.UserNotFoundException;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingUsers;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserJpaRepositoryAdapter implements ForAccessingUsers {
    private final UserRepository userRepository;
    private final UserDbEntityMapper mapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User createUser(UserSignupData userSignupData) {
        var entity = mapper.toUserDbEntity(userSignupData);
        entity = userRepository.save(entity);
        return mapper.toUser(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.countByEmail(email) > 0;
    }

    @Override
    public User updateUser(UserId userId, UserUpdateData userUpdateData) {
        var userDbEntity = userRepository.findById(userId.value()).orElseThrow(UserNotFoundException::new);
        userDbEntity.setEmail(userUpdateData.email());
        userDbEntity.setName(userUpdateData.name());
        userDbEntity.setAccountType(userUpdateData.accountType());
        userDbEntity.setPassword(passwordEncoder.encode(userUpdateData.newPassword())); // todo: move to mapper
        userDbEntity = userRepository.save(userDbEntity);
        return mapper.toUser(userDbEntity);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        Optional<UserDbEntity> userDbEntity = userRepository.findById(userId.value());
        return userDbEntity.map(mapper::toUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserDbEntity> userDbEntity = userRepository.findByEmail(email);
        return userDbEntity.map(mapper::toUser);
    }

    @Override
    public Optional<User> findByUlid(UserUlid userUlid) {
        Optional<UserDbEntity> userDbEntity = userRepository.findByUlid(userUlid.value());
        return userDbEntity.map(mapper::toUser);
    }
}
