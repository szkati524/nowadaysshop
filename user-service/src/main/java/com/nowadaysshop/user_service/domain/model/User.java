package com.nowadaysshop.user_service.domain.model;


import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Wallet wallet;

    public User(UUID id, String email, String firstName, String lastName, Wallet wallet) {
        this.id = id != null ? id : UUID.randomUUID();
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;

        this.wallet = wallet != null ? wallet : new Wallet(null, null);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
