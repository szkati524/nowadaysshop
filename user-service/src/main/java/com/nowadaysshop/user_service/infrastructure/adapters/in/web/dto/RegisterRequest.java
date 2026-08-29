package com.nowadaysshop.user_service.infrastructure.adapters.in.web.dto;

public record RegisterRequest(String email,String firstName,String lastName,String password) {
}
