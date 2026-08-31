package catalog_service;

import catalog_service.application.ports.out.ProductRepositoryPort;
import catalog_service.application.service.ProductApplicationService;
import catalog_service.domain.exception.InsufficientStockException;
import catalog_service.domain.model.Product;
import catalog_service.domain.service.InventoryDomainService;
import jakarta.persistence.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductApplicationServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Spy
    private InventoryDomainService inventoryDomainService = new InventoryDomainService();

    @InjectMocks
    private ProductApplicationService productApplicationService;

    @Test
    void shouldReserveStockSuccessfullyTest() {
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Laptop", "Gaming Laptop", BigDecimal.valueOf(3500), 10, "ELECTRONICS");
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(product));

        productApplicationService.reserveStock(productId, 3);

        assertEquals(7, product.getStockQuantity());
        verify(productRepositoryPort, times(1)).save(product);
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughStockTest() {
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Myszka", "Myszka bezprzewodowa", BigDecimal.valueOf(100), 2, "ELECTRONICS");
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> {
            productApplicationService.reserveStock(productId, 5);
        });

        verify(productRepositoryPort, never()).save(product);
    }
}