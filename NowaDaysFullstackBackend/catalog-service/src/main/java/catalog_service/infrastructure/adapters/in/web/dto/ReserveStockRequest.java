package catalog_service.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReserveStockRequest(
        @NotNull(message = "Ilość jest wymagana")
        @Min(value = 1,message = "Ilość musi być większa od zera")
        Integer quantity
) {
}
