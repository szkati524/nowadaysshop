package com.example.order_service.infrastructure.config;

import com.example.order_service.application.ports.out.CatalogClientPort;
import com.example.order_service.application.ports.out.OrderRepositoryPort;
import com.example.order_service.application.ports.out.UserClientPort;
import com.example.order_service.application.service.OrderApplicationService;
import com.example.order_service.domain.service.OrderDomainService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DomainConfig {
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(){
        return RestClient.builder();
    }
    @Bean
    public OrderDomainService orderDomainService(){
        return new OrderDomainService();
    }
    @Bean
    public OrderApplicationService orderApplicationService(
            OrderRepositoryPort orderRepositoryPort,
            UserClientPort userClientPort,
            CatalogClientPort catalogClientPort,
            OrderDomainService orderDomainService, RabbitTemplate rabbitTemplate) {
        return new OrderApplicationService(orderRepositoryPort,userClientPort,catalogClientPort,orderDomainService, rabbitTemplate);
    }
}
