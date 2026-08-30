package catalog_service.application.ports.in;

import catalog_service.domain.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductUseCase  {
    Product createProduct(String name, String description, BigDecimal price,Integer initialStock);
    Product getProductById(UUID id );
    List<Product> getAllProduct();
    void reserveStock(UUID productId,int quantity);
    void addStock(UUID productId,int quantity);
}
