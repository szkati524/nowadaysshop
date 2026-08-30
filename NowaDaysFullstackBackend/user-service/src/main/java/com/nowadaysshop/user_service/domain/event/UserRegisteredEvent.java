package com.nowadaysshop.user_service.domain.event;

import java.io.Serializable;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email,
        String firstName
) implements Serializable {
}
