package com.example.order_service.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "ID użytkownika jest wymagane")
        UUID userId,
        @NotNull(message = "ID produktu jest wymagane")
        UUID productId,
        @NotNull(message = "Ilość jest wymagana")
        @Min(value = 1,message = "Ilość musi byc większa od 0")
        Integer quantity

) {
}
