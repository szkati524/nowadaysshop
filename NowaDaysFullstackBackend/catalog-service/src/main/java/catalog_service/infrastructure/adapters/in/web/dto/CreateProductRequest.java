package catalog_service.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "Nazwa produktu jest wymagana")
        String name,
        String description,
        @NotNull(message = "Cena jest wymagana")
        @PositiveOrZero(message = "Cena nie może być ujemna")
        BigDecimal price,
        @NotNull(message = "Stan początkowy jest wymagany")
        @Min(value = 0,message = "Stan początkowy nie może być ujemny")
        Integer initialStock
) {
}
