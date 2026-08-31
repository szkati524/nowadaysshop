package catalog_service.application.service;
import catalog_service.domain.exception.InsufficientStockException;
import catalog_service.domain.model.Product;
import catalog_service.domain.service.InventoryDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
public class InventoryDomainServiceTest {

    private InventoryDomainService inventoryDomainService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        inventoryDomainService = new InventoryDomainService();
        testProduct = new Product(
                UUID.randomUUID(),
                "Mysz Bezprzewodowa",
                "Bezprzewodowa mysz optyczna",
                new BigDecimal("150.00"),
                20,
                "ELECTRONICS"
        );
    }

    @Test
    void shouldProcessStockReservationTest() {
        inventoryDomainService.processStockReservation(testProduct, 5);

        assertEquals(15, testProduct.getStockQuantity());
    }

    @Test
    void shouldThrowExceptionOnInsufficientStockDuringReservationTest() {
        assertThrows(
                InsufficientStockException.class,
                () -> inventoryDomainService.processStockReservation(testProduct, 25)
        );
    }

    @Test
    void shouldProcessStockRestockTest() {
        inventoryDomainService.processStockRestock(testProduct, 10);

        assertEquals(30, testProduct.getStockQuantity());
    }
}
