package com.auth.auth_service.user.infrastructure.adapter.output.persistence;

import com.auth.auth_service.user.application.port.output.UserOutputPort;
import com.auth.auth_service.user.domain.model.User;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.auth.auth_service.user.infrastructure.adapter.output.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserOutputPort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public User saveUser(User user) {
        UserEntity userEntity = userPersistenceMapper.toUserEntity(user);
        userEntity = userRepository.save(userEntity);

        return userPersistenceMapper.toUser(userEntity);
    }

    @Override
    public User deleteUser(User user) {
        UserEntity userEntity = userPersistenceMapper.toUserEntity(user);
        userRepository.delete(userEntity);

        return userPersistenceMapper.toUser(userEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        final Optional<UserEntity> userEntity = userRepository.findByUsername(username);

        if(userEntity.isEmpty())
            return Optional.empty();

        final User user = userPersistenceMapper.toUser(userEntity.get());

        return Optional.of(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if(userEntity.isEmpty())
            return Optional.empty();

        final User user = userPersistenceMapper.toUser(userEntity.get());

        return Optional.of(user);
    }

    @Override
    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
