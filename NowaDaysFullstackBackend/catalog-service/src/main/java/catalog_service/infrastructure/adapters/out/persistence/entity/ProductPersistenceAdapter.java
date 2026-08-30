package catalog_service.infrastructure.adapters.out.persistence.entity;

import catalog_service.application.ports.out.ProductRepositoryPort;
import catalog_service.domain.model.Product;
import catalog_service.infrastructure.adapters.out.repository.SpringDataProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository repository;

    public ProductPersistenceAdapter(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapToEntity(product);
        ProductEntity savedEntity = repository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream()
                .map(this::mapToDomain)
                .toList();
    }
    private ProductEntity mapToEntity(Product product){
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );

    }
    private Product mapToDomain(ProductEntity entity){
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStockQuantity()
        );
    }
}
