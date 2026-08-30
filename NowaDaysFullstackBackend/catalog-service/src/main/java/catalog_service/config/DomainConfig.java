package catalog_service.config;

import catalog_service.application.ports.out.ProductRepositoryPort;
import catalog_service.application.service.ProductApplicationService;
import catalog_service.domain.service.InventoryDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public InventoryDomainService inventoryDomainService(){
        return new InventoryDomainService();
    }
    @Bean
    public ProductApplicationService productApplicationService(
            ProductRepositoryPort productRepositoryPort,
            InventoryDomainService inventoryDomainService) {
        return new ProductApplicationService(productRepositoryPort,inventoryDomainService);
    }

}
