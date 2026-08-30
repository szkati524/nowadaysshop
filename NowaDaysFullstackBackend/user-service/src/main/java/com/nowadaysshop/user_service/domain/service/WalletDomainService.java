package com.nowadaysshop.user_service.domain.service;

import com.nowadaysshop.user_service.domain.model.User;

import java.math.BigDecimal;

public class WalletDomainService {

    public void processDeposit(User user, BigDecimal amount){
        user.getWallet().deposit(amount);
    }
    public void processWithdrawal(User user,BigDecimal amount){
        user.getWallet().withdraw(amount);
    }
}
