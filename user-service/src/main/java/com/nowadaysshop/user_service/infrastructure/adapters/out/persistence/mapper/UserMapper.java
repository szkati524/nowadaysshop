package com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.mapper;

import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;
import com.nowadaysshop.user_service.domain.roles.Role;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity){
        if (entity == null) return null;
        Wallet wallet = new Wallet(entity.getWalletId(),entity.getBalance());
        Role role = entity.getRole() != null ? Role.valueOf(entity.getRole()) : null;
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPassword(),
                role,
                wallet
        );

    }
    public static UserEntity toEntity(User domain){
        if (domain == null) return null;

        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .password(domain.getPassword())
                .role(domain.getRole())
                .walletId(domain.getWallet() != null ? domain.getWallet().getId() : null)
                .balance(domain.getWallet() != null ? domain.getWallet().getBalance() : null)
                .build();
    }
}
