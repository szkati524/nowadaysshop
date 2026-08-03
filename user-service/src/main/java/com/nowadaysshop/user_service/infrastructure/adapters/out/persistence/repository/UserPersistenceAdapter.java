package com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.repository;

import com.nowadaysshop.user_service.application.ports.out.UserRepositoryPort;
import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {
    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }


    @Override
    public User save(User user) {
        UserEntity entity = mapToEntity(user);
        UserEntity savedEntity = repository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::mapToDomain);
    }
    private UserEntity mapToEntity(User user){
        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getWallet().getId(),
                user.getWallet().getBalance()

        );

    }
    private User mapToDomain(UserEntity entity){
        Wallet wallet = new Wallet(entity.getWalletId(),entity.getBalance());
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                wallet
        );
    }
}
