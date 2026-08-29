package com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.mapper;
import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;
import com.nowadaysshop.user_service.domain.roles.Role;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
public class UserMapperTest {

    @Test

    void shouldMapUserToUserEntitySuccessfullyTest() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, new BigDecimal("250.00"));
        User domainUser = new User(userId, "test@shop.com", "Jan", "Kowalski", "hashedPass", Role.ROLE_USER, wallet);

        UserEntity entity = UserMapper.toEntity(domainUser);

        assertNotNull(entity);
        assertEquals(userId, entity.getId());
        assertEquals("test@shop.com", entity.getEmail());
        assertEquals("Jan", entity.getFirstName());
        assertEquals("Kowalski", entity.getLastName());
        assertEquals("hashedPass", entity.getPassword());


        assertEquals("ROLE_USER", entity.getRole());

        assertEquals(walletId, entity.getWalletId());
        assertEquals(new BigDecimal("250.00"), entity.getBalance());
    }

    @Test

    void shouldMapUserEntityToUserSuccessfullyWithNonNullWalletTest() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UserEntity entity = UserEntity.builder()
                .id(userId)
                .email("test@shop.com")
                .firstName("Jan")
                .lastName("Kowalski")
                .password("hashedPass")
                .role("ROLE_USER")
                .walletId(walletId)
                .balance(new BigDecimal("250.00"))
                .build();

        User domainUser = UserMapper.toDomain(entity);

        assertNotNull(domainUser);
        assertEquals(userId, domainUser.getId());


        assertEquals("ROLE_USER", domainUser.getRole());

        assertNotNull(domainUser.getWallet(), "Portfel nie może być null po zmapowaniu z encji!");
        assertEquals(walletId, domainUser.getWallet().getId());
        assertEquals(0, new BigDecimal("250.00").compareTo(domainUser.getWallet().getBalance()));
    }

    @Test

    void shouldReturnNullWhenMappingNullObjectsTest() {
        assertNull(UserMapper.toDomain(null));
        assertNull(UserMapper.toEntity(null));
    }
}


