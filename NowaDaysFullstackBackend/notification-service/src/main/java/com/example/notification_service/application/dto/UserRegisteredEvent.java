package com.example.notification_service.application.dto;

import java.util.UUID;

public record UserRegisteredEvent(UUID userId,String email,String firstName) {
}
