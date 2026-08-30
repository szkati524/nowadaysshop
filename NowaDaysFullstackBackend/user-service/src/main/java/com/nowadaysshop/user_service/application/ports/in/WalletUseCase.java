package com.nowadaysshop.user_service.application.ports.in;

import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.roles.Role;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletUseCase {


    User createUser(String email, String firstName, String lastName, String rawPassword, Role role);

    User getUserById(UUID userId);
    BigDecimal getBalance(UUID userId);
    void deposit(UUID userId,BigDecimal amount);
    void withdraw (UUID userId,BigDecimal amount);
}
