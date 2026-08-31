package catalog_service.infrastructure.adapters.in.web.dto;

import java.util.List;

public record PagedResult<T>(
        List<T> content,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
