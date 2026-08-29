package com.nowadaysshop.user_service.application.service;

import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;

import com.nowadaysshop.user_service.domain.roles.Role;
import com.nowadaysshop.user_service.domain.service.WalletDomainService;
import com.nowadaysshop.user_service.domain.exception.InSufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletDomainServiceTest {

    private WalletDomainService walletDomainService;
    private User testUser;

    @BeforeEach
    void setUp() {
        walletDomainService = new WalletDomainService();

        Wallet wallet = new Wallet(UUID.randomUUID(), new BigDecimal("100.00"));
        testUser = new User(UUID.randomUUID(), "test@example.com", "Jan", "Kowalski", "hashedPassword", Role.ROLE_USER, wallet);
    }

    @Test

    void shouldProcessDepositSuccessfullyTest() {

        BigDecimal depositAmount = new BigDecimal("50.00");


        walletDomainService.processDeposit(testUser, depositAmount);


        assertEquals(new BigDecimal("150.00"), testUser.getWallet().getBalance());
    }

    @Test

    void shouldThrowExceptionWhenWithdrawalAmountExceedsBalanceTest() {

        BigDecimal withdrawalAmount = new BigDecimal("150.00");


        assertThrows(InSufficientFundsException.class, () ->
                walletDomainService.processWithdrawal(testUser, withdrawalAmount)
        );
    }
}

