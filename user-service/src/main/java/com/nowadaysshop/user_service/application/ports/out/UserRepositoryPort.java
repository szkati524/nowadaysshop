package com.nowadaysshop.user_service.application.ports.out;

import com.nowadaysshop.user_service.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
}
