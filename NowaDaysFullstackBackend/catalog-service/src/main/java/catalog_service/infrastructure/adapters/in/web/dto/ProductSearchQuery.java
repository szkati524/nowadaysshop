package catalog_service.infrastructure.adapters.in.web.dto;

import java.math.BigDecimal;

public record ProductSearchQuery(
        String name,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public ProductSearchQuery{
        if (size <= 0) size = 10;
        if (page <= 0) page = 0;
        if (sortBy == null || sortBy.isBlank()) sortBy = "name";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "asc";
    }
}
