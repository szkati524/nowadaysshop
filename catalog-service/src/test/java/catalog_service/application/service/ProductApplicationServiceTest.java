package catalog_service.application.service;
import catalog_service.application.ports.out.ProductRepositoryPort;
import catalog_service.domain.exception.ProductNotFoundException;
import catalog_service.domain.model.Product;
import catalog_service.domain.service.InventoryDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ProductApplicationServiceTest {
    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private InventoryDomainService inventoryDomainService;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    private Product sampleProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sampleProduct = new Product(
                productId,
                "Monitor 4K",
                "Opis monitora",
                new BigDecimal("1200.00"),
                15
        );
    }

    @Test

    void shouldCreateProductSuccessfullyTest() {

        when(productRepositoryPort.save(any(Product.class))).thenReturn(sampleProduct);


        Product createdProduct = productApplicationService.createProduct(
                "Monitor 4K",
                "Opis monitora",
                new BigDecimal("1200.00"),
                15
        );


        assertNotNull(createdProduct);
        assertEquals("Monitor 4K", createdProduct.getName());
        verify(productRepositoryPort, times(1)).save(any(Product.class));
    }

    @Test

    void shouldGetProductByIdTest() {

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(sampleProduct));


        Product foundProduct = productApplicationService.getProductById(productId);


        assertNotNull(foundProduct);
        assertEquals(productId, foundProduct.getId());
        verify(productRepositoryPort, times(1)).findById(productId);
    }

    @Test

    void shouldThrowExceptionWhenProductNotFoundByIdTest() {

        UUID nonExistingId = UUID.randomUUID();
        when(productRepositoryPort.findById(nonExistingId)).thenReturn(Optional.empty());


        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productApplicationService.getProductById(nonExistingId)
        );

        assertTrue(exception.getMessage().contains("Nie znaleziono produktu o ID"));
        verify(productRepositoryPort, times(1)).findById(nonExistingId);
    }

    @Test

    void shouldGetAllProductsTest() {

        when(productRepositoryPort.findAll()).thenReturn(List.of(sampleProduct));


        List<Product> products = productApplicationService.getAllProduct();


        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepositoryPort, times(1)).findAll();
    }

    @Test

    void shouldReserveStockSuccessfullyTest() {

        int quantityToReserve = 5;
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(sampleProduct));


        productApplicationService.reserveStock(productId, quantityToReserve);


        verify(productRepositoryPort, times(1)).findById(productId);
        verify(inventoryDomainService, times(1)).processStockReservation(sampleProduct, quantityToReserve);
        verify(productRepositoryPort, times(1)).save(sampleProduct);
    }

    @Test

    void shouldAddStockSuccessfullyTest() {

        int quantityToAdd = 10;
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(sampleProduct));


        productApplicationService.addStock(productId, quantityToAdd);


        verify(productRepositoryPort, times(1)).findById(productId);
        verify(inventoryDomainService, times(1)).processStockRestock(sampleProduct, quantityToAdd);
        verify(productRepositoryPort, times(1)).save(sampleProduct);
    }
}


