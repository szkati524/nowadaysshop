package com.nowadaysshop.user_service.infrastructure.adapters.out.persistence;

import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.repository.UserPersistenceAdapter;
import org.hibernate.dialect.PostgreSQLDialect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@Import(UserPersistenceAdapter.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserPersistenceAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

    }
    @Autowired
    private UserPersistenceAdapter userPersistenceAdapter;

    @Test
    void shouldSaveAndFindUserInPostgresTest(){
        User user = new User(UUID.randomUUID(),"john@example.com", "John", "Doe", new Wallet(null, BigDecimal.valueOf(500)));
        userPersistenceAdapter.save(user);
        Optional<User> foundUser = userPersistenceAdapter.findByEmail("john@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("John",foundUser.get().getFirstName());
        assertEquals(0,BigDecimal.valueOf(50).compareTo(foundUser.get().getWallet().getBalance()));
    }

}
