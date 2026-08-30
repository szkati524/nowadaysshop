package com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WalletOperationRequest(
        @NotNull(message = "Kwota jest wymagana")
        @Positive(message = "Kwota musi być większa od zera")
        BigDecimal amount
) {
}
