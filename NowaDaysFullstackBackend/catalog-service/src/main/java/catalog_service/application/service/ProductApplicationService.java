package catalog_service.application.service;

import catalog_service.application.ports.in.ProductUseCase;
import catalog_service.application.ports.out.ProductRepositoryPort;
import catalog_service.domain.exception.ProductNotFoundException;
import catalog_service.domain.model.Product;
import catalog_service.domain.service.InventoryDomainService;
import catalog_service.infrastructure.adapters.in.web.dto.PagedResult;
import catalog_service.infrastructure.adapters.in.web.dto.ProductSearchQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductApplicationService implements ProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final InventoryDomainService inventoryDomainService;

    public ProductApplicationService(ProductRepositoryPort productRepositoryPort, InventoryDomainService inventoryDomainService) {
        this.productRepositoryPort = productRepositoryPort;
        this.inventoryDomainService = inventoryDomainService;
    }

    @Override
    public Product createProduct(String name, String description, BigDecimal price, Integer initialStock, String category) {
       Product product = new Product(null,name,description,price,initialStock,category);
       return productRepositoryPort.save(product);
    }




    @Override
    public Product getProductById(UUID id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Nie znaleziono produktu o ID: " + id));
    }
    @Override
    public PagedResult<Product> searchProducts(ProductSearchQuery query) {
        return productRepositoryPort.searchProducts(query);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepositoryPort.findAll();
    }

    @Override
    public void reserveStock(UUID productId, int quantity) {
Product product = getProductById(productId);
inventoryDomainService.processStockReservation(product,quantity);
productRepositoryPort.save(product);
    }

    @Override
    public void addStock(UUID productId, int quantity) {
Product product = getProductById(productId);
inventoryDomainService.processStockRestock(product,quantity);
productRepositoryPort.save(product);
    }
}
