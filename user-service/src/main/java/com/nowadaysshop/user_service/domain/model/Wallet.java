package com.nowadaysshop.user_service.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Wallet {
    private final UUID id;
    private BigDecimal balance;

    public Wallet(UUID id, BigDecimal balance) {
        this.id = id != null ? id: UUID.randomUUID();
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }
    public void deposit(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Kwota wpłaty musi być większa od 0");
        }
        this.balance = this.balance.add(amount);
    }
    public void withdraw(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Kwota wypłaty musi być większa od 0");
        }
        this.balance = this.balance.subtract(amount);
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
