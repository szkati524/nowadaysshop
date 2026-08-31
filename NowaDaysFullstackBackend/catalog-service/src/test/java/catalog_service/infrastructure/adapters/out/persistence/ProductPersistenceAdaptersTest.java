package catalog_service.infrastructure.adapters.out.persistence;

import catalog_service.domain.model.Product;
import catalog_service.infrastructure.adapters.out.persistence.entity.ProductPersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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
@Import(ProductPersistenceAdapter.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductPersistenceAdaptersTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_catalog_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
            }
            @Autowired
    private ProductPersistenceAdapter productPersistenceAdapter;

    @Test
    void shouldSaveAndFindProductInPostgres(){
        Product product = new Product(UUID.randomUUID(),"Klawiatura","Mechaniczna", BigDecimal.valueOf(250),15,"IT");
        productPersistenceAdapter.save(product);
        Optional<Product> foundProduct = productPersistenceAdapter.findById(product.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Klawiatura",foundProduct.get().getName());
        assertEquals(15,foundProduct.get().getStockQuantity());
    }

}
