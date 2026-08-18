package com.nowadaysshop.user_service.infrastructure.adapters.in.web;

import com.nowadaysshop.user_service.application.ports.in.WalletUseCase;
import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto.CreateUserRequest;
import com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto.WalletOperationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final WalletUseCase walletUseCase;

    public UserController(WalletUseCase walletUseCase) {
        this.walletUseCase = walletUseCase;
    }
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User createdUser = walletUseCase.createUser(request.email(), request.firstName(), request.lastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id){
        return ResponseEntity.ok(walletUseCase.getUserById(id));
    }
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id){
        return ResponseEntity.ok(walletUseCase.getBalance(id));
    }
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable UUID id, @Valid @RequestBody WalletOperationRequest request){
        walletUseCase.deposit(id,request.amount());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable UUID id,@Valid @RequestBody WalletOperationRequest request){
        walletUseCase.withdraw(id,request.amount());
        return ResponseEntity.ok().build();
    }
}
