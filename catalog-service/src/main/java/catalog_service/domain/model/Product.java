package catalog_service.domain.model;

import catalog_service.domain.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    private final UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;

    public Product(UUID id, String name, String description, BigDecimal price, Integer stockQuantity) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cena nie może być ujemna");
        }
        if (stockQuantity != null && stockQuantity < 0) {
            throw new IllegalArgumentException("Stan magazynowy nie może być ujemny");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
    }
    public void reserveStock(int quantity){
        if (quantity <= 0){
            throw new IllegalArgumentException("Ilość do zarezerwowana musi być większa od 0");
        }
        if (this.stockQuantity < quantity){
            throw new InsufficientStockException(
                    "Niewystarczająca ilość produktu w magazynie. Dostępne: " + this.stockQuantity + ", wymagane: " + quantity
            );

        }
        this.stockQuantity -= quantity;
    }


public void addStock(int quantity){
    if (quantity <= 0){
        throw new IllegalArgumentException("Ilość do dodania musi być większa od 0");
    }
    this.stockQuantity += quantity;
}

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
}