package com.example.notification_service.infrastructure.adapters.in.messaging;


import com.example.notification_service.application.dto.OrderCreatedEvent;
import com.example.notification_service.application.dto.UserRegisteredEvent;
import com.example.notification_service.infrastructure.config.RabbitMQConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;



@Component
public class NotificationEventListeners {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListeners.class);
    @RabbitListener(queues = RabbitMQConsumerConfig.USER_QUEUE)
    public void handleUserRegistered(UserRegisteredEvent event){
        log.info("[E-MAIL SERVICE] Sending welcome email to user: {} (email: {})",
                event.firstName(), event.email());
    }
    @RabbitListener(queues = RabbitMQConsumerConfig.ORDER_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event){
        log.info("[E-MAIL SERVICE] Sending order confirmation email for Order ID: {} (User ID: {}, Amount: ${})",
                event.orderId(), event.userId(), event.totalAmount());
    }
}
