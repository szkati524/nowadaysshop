package catalog_service.infrastructure.adapters.out.repository;

import catalog_service.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, UUID> {
}
