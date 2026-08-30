package com.nowadaysshop.user_service.application.service;

import com.nowadaysshop.user_service.application.ports.in.WalletUseCase;
import com.nowadaysshop.user_service.application.ports.out.UserRepositoryPort;
import com.nowadaysshop.user_service.config.RabbitMQConfig;
import com.nowadaysshop.user_service.domain.event.UserRegisteredEvent;
import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.roles.Role;
import com.nowadaysshop.user_service.domain.service.WalletDomainService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.UUID;

public class UserApplicationService implements WalletUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final WalletDomainService walletDomainService;

    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserApplicationService(UserRepositoryPort userRepositoryPort, WalletDomainService walletDomainService, RabbitTemplate rabbitTemplate, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.walletDomainService = walletDomainService;
        this.rabbitTemplate = rabbitTemplate;
        this.passwordEncoder = passwordEncoder;
    }




    @Override
    public User createUser(String email, String firstName, String lastName, String rawPassword, Role role) {
        userRepositoryPort.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Użytkownik o podanym emailu już istnieje: " + email);
        });
        String encodedPassword = passwordEncoder.encode(rawPassword );
User newUser = new User(null,email,firstName,lastName, rawPassword, role, null);
        User savedUser = userRepositoryPort.save(newUser);


        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "user.registered", event);


        return savedUser;
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
