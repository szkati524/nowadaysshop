package catalog_service.infrastructure.adapters.out.repository;

import catalog_service.infrastructure.adapters.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {

    @Query("SELECT DISTINCT p.category FROM ProductEntity p WHERE p.category IS NOT NULL AND p.category <> '' ORDER BY p.category ASC")
    List<String> findDistinctCategories();
}
