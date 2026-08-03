package com.nowadaysshop.user_service.application.service;

import com.nowadaysshop.user_service.application.ports.in.WalletUseCase;
import com.nowadaysshop.user_service.application.ports.out.UserRepositoryPort;
import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.service.WalletDomainService;

import java.math.BigDecimal;
import java.util.UUID;

public class UserApplicationService implements WalletUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final WalletDomainService walletDomainService;

    public UserApplicationService(UserRepositoryPort userRepositoryPort, WalletDomainService walletDomainService) {
        this.userRepositoryPort = userRepositoryPort;
        this.walletDomainService = walletDomainService;
    }

    @Override
    public User createUser(String email, String firstName, String lastName) {
        userRepositoryPort.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Użytkownik o podanym emailu już istnieje: " + email);
        });
User newUser = new User(null,email,firstName,lastName,null);
return userRepositoryPort.save(newUser);
    }

    @Override
    public User getUserById(UUID userId) {
      return userRepositoryPort.findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o ID: " + userId));
    }

    @Override
    public BigDecimal getBalance(UUID userId) {
        User user = getUserById(userId);
        return user.getWallet().getBalance();
    }

    @Override
    public void deposit(UUID userId, BigDecimal amount) {
User user = getUserById(userId);
walletDomainService.processDeposit(user,amount);
userRepositoryPort.save(user);
    }

    @Override
    public void withdraw(UUID userId, BigDecimal amount) {
User user = getUserById(userId);
walletDomainService.processWithdrawal(user,amount);
userRepositoryPort.save(user);
    }
}
