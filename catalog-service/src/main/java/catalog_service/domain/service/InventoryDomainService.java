package catalog_service.domain.service;

import catalog_service.domain.model.Product;

public class InventoryDomainService {

    public void processStockReservation(Product product,int quantity){
        product.reserveStock(quantity);
    }
    public void processStockRestock(Product product,int quantity){
        product.addStock(quantity);
    }
}
