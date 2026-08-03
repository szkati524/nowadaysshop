package com.nowadaysshop.user_service.config;

import com.nowadaysshop.user_service.application.ports.out.UserRepositoryPort;
import com.nowadaysshop.user_service.application.service.UserApplicationService;
import com.nowadaysshop.user_service.domain.service.WalletDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {
    @Bean
    public WalletDomainService walletDomainService(){
        return new WalletDomainService();
    }
    @Bean
    public UserApplicationService userApplicationService(
            UserRepositoryPort userRepositoryPort,
            WalletDomainService walletDomainService
    ) {
        return new UserApplicationService(userRepositoryPort,walletDomainService);
    }
}
