package com.nowadaysshop.user_service.domain.model;


import com.nowadaysshop.user_service.domain.roles.Role;

import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String password;
    private final String role;
    private final Wallet wallet;

    public User(UUID id, String email, String firstName, String lastName, String password, Role role, Wallet wallet) {
        this.id = id != null ? id : UUID.randomUUID();
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role != null ? String.valueOf(role) : "ROLE_USER";

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

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public Wallet getWallet() {
        return wallet;
    }
}
