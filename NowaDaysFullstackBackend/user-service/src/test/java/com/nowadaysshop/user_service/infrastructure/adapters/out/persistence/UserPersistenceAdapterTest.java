package com.nowadaysshop.user_service.infrastructure.adapters.out.persistence;


import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;
import com.nowadaysshop.user_service.domain.roles.Role;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.repository.UserPersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import(UserPersistenceAdapter.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

    @Test
    void shouldSaveAndFindUserInPostgresTest() {
        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "john@example.com",
                "John",
                "Doe",
                "hashed_password123",
                Role.ROLE_USER,
                new Wallet(null, BigDecimal.valueOf(500))
        );

        userPersistenceAdapter.save(user);
        Optional<User> foundUser = userPersistenceAdapter.findByEmail("john@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
    }
}