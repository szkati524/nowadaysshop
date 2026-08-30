package com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank(message = "Email jest wymagany") @Email String email,@NotBlank(message = "Imię jest wymagane") String firstName,@NotBlank(message = "Nazwisko jest wymagane") String lastName,@NotBlank(message = "Hasło jest wymagane") String password) {
}
