package com.nowadaysshop.user_service;

import com.nowadaysshop.user_service.application.ports.out.UserRepositoryPort;
import com.nowadaysshop.user_service.application.service.UserApplicationService;
import com.nowadaysshop.user_service.domain.exception.InSufficientFundsException;
import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;
import com.nowadaysshop.user_service.domain.service.WalletDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    private UserApplicationService userApplicationService;

    @BeforeEach
    void setUp(){
        WalletDomainService walletDomainService = new WalletDomainService();
        userApplicationService = new UserApplicationService(userRepositoryPort,walletDomainService);

    }
    @Test
    void shouldDepositMoneySuccessfullyTest(){
        UUID userId = UUID.randomUUID();
        User user = new User(userId,"test@shop.com","Jan","Kowalski",new Wallet(null, BigDecimal.valueOf(100)));
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));

        userApplicationService.deposit(userId,BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(150),user.getWallet().getBalance());
        verify(userRepositoryPort,times(1)).save(user);
    }
    @Test
    void shouldThrowExceptionWhenWithdrawalExceedsBalanceTest(){
        UUID userId = UUID.randomUUID();
        User user = new User(userId,"test@shop.com","Jan","Kowalski",new Wallet(null,BigDecimal.valueOf(100)));
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(InSufficientFundsException.class, () -> {
            userApplicationService.withdraw(userId,BigDecimal.valueOf(150));
        });
        verify(userRepositoryPort,never()).save(user);
    }
}
