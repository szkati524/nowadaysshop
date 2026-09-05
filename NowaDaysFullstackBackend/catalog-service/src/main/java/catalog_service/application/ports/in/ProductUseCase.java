package catalog_service.application.ports.in;

import catalog_service.domain.model.Product;
import catalog_service.infrastructure.adapters.in.web.dto.PagedResult;
import catalog_service.infrastructure.adapters.in.web.dto.ProductSearchQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductUseCase  {
    Product createProduct(String name, String description, BigDecimal price, Integer initialStock, String category);


    Product getProductById(UUID id );

    PagedResult<Product> searchProducts(ProductSearchQuery query);

    List<Product> getAllProduct();
    void reserveStock(UUID productId,int quantity);
    void addStock(UUID productId,int quantity);
    List<String> getDistinctCategories();
}
