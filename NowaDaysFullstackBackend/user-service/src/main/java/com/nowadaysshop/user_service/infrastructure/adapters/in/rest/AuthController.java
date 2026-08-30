package com.nowadaysshop.user_service.infrastructure.adapters.in.rest;

import com.nowadaysshop.user_service.domain.service.AuthService;
import com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto.AuthResponse;
import com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto.LoginRequest;
import com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        try {


            String response = authService.register(
                    request.email(),
                    request.firstName(),
                    request.lastName(),
                    request.password()
            );
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage()  );
        }
        }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request ){
        try {
            String token = authService.login(request.email(), request.password());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", request.email()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
