package com.example.order_service.application.ports.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserClientPort {
void chargeWallet(UUID userId, BigDecimal amount);

}
