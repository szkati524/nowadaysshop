package com.nowadaysshop.user_service.config;

import com.nowadaysshop.user_service.application.ports.out.UserRepositoryPort;
import com.nowadaysshop.user_service.application.service.UserApplicationService;
import com.nowadaysshop.user_service.domain.service.WalletDomainService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DomainConfig {
    @Bean
    public WalletDomainService walletDomainService(){
        return new WalletDomainService();
    }
    @Bean
    public UserApplicationService userApplicationService(
            UserRepositoryPort userRepositoryPort,
            WalletDomainService walletDomainService,
            RabbitTemplate rabbitTemplate,
            PasswordEncoder passwordEncoder
    ) {
        return new UserApplicationService(userRepositoryPort,walletDomainService, rabbitTemplate, passwordEncoder);
    }
}
