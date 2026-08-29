package com.nowadaysshop.user_service.domain.service;

import com.nowadaysshop.user_service.config.security.JwtUtils;
import com.nowadaysshop.user_service.domain.model.User;
import com.nowadaysshop.user_service.domain.model.Wallet;
import com.nowadaysshop.user_service.domain.roles.Role;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.mapper.UserMapper;
import com.nowadaysshop.user_service.infrastructure.adapters.out.persistence.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SpringDataUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public String register(String email,String firstName,String lastName,String rawPassword){
        if (userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Użytkownik o podanym adresie e-mail już istnieje!");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.ZERO);
        User user = new User(
                UUID.randomUUID(),
                email,
                firstName,
                lastName,
                encodedPassword,
                Role.ROLE_USER,
                wallet
        );
        UserEntity entity = UserMapper.toEntity(user);
        userRepository.save(entity);
        return "Użytkownik zarejestrowany pomyślnie!";
    }
    public String login(String email,String rawPassword){
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy e-mail lub hasło"));

        if (!passwordEncoder.matches(rawPassword, entity.getPassword())){
            throw new IllegalArgumentException("Nieprawidłowy e-mail lub hasło");
        }
        return jwtUtils.generateToken(entity.getId(),entity.getRole());
    }
}
