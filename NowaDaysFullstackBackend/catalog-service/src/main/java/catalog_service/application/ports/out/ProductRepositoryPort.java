package catalog_service.application.ports.out;

import catalog_service.domain.model.Product;

import javax.sound.sampled.Port;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {

    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
}
