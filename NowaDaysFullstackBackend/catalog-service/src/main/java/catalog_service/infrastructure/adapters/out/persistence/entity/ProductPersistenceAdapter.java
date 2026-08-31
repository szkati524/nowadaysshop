package catalog_service.infrastructure.adapters.out.persistence.entity;

import catalog_service.application.ports.out.ProductRepositoryPort;
import catalog_service.domain.model.Product;
import catalog_service.infrastructure.adapters.in.web.dto.PagedResult;
import catalog_service.infrastructure.adapters.in.web.dto.ProductSearchQuery;
import catalog_service.infrastructure.adapters.out.repository.SpringDataProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    @Override
    public PagedResult<Product> searchProducts(ProductSearchQuery query) {
        Specification<ProductEntity> spec = (root,queryFactory,cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.name() != null && !query.name().isBlank()){
                predicates.add(cb.like(cb.lower(root.get("name")),query.name().trim().toLowerCase() + "%"));
            }
            if (query.category() != null && !query.category().isBlank()){
                predicates.add(cb.equal(cb.lower(root.get("category")),query.category().trim().toLowerCase()));
            }
            if (query.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), query.minPrice()));
            }
            if (query.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), query.maxPrice()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort.Direction direction = query.sortDirection().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction,query.sortBy());
        Pageable pageable = PageRequest.of(query.page(),query.size(),sort);
        Page<ProductEntity> entityPage = repository.findAll(spec,pageable);
        List<Product> content = entityPage.getContent().stream()
                .map(this::mapToDomain)
                .toList();
        return new PagedResult<>(
                content,
                entityPage.getNumber(),
                entityPage.getTotalPages(),
                entityPage.getTotalElements()
        );
    }

    private ProductEntity mapToEntity(Product product){
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory()
        );

    }
    private Product mapToDomain(ProductEntity entity){
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.getCategory()
        );
    }
}
