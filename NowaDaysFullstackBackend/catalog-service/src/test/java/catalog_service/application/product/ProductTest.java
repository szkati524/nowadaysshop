package catalog_service.application.product;

import catalog_service.domain.exception.InsufficientStockException;
import catalog_service.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(
                UUID.randomUUID(),
                "Klawiatura Mechaniczna",
                "Opis klawiatury",
                new BigDecimal("299.99"),
                10
        );
    }

    @Test

    void shouldThrowExceptionWhenPriceIsNegativeTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(UUID.randomUUID(), "Test", "Opis", new BigDecimal("-10.00"), 5)
        );
    }

    @Test

    void shouldThrowExceptionWhenStockIsNegativeTest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Product(UUID.randomUUID(), "Test", "Opis", new BigDecimal("10.00"), -5)
        );
    }

    @Test

    void shouldReserveStockSuccessfullyTest() {
        product.reserveStock(3);
        assertEquals(7, product.getStockQuantity());
    }

    @Test

    void shouldThrowExceptionWhenReservingMoreThanAvailableTest() {
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> product.reserveStock(15)
        );

        assertTrue(exception.getMessage().contains("Niewystarczająca ilość produktu w magazynie"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})

    void shouldThrowExceptionWhenReservingInvalidQuantityTest(int invalidQuantity) {
        assertThrows(
                IllegalArgumentException.class,
                () -> product.reserveStock(invalidQuantity)
        );
    }

    @Test

    void shouldAddStockSuccessfullyTest() {
        product.addStock(5);
        assertEquals(15, product.getStockQuantity());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})

    void shouldThrowExceptionWhenAddingInvalidQuantityTest(int invalidQuantity) {
        assertThrows(
                IllegalArgumentException.class,
                () -> product.addStock(invalidQuantity)
        );
    }
}

