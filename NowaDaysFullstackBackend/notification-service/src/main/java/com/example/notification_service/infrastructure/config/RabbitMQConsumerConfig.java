package com.example.notification_service.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConsumerConfig {
    public static final String USER_QUEUE = "notification.user.registered.queue";
    public static final String ORDER_QUEUE = "notification.order.created.queue";
    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(USER_QUEUE, true);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("user.exchange");
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order.exchange");
    }

    @Bean
    public Binding userBinding(Queue userRegisteredQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userRegisteredQueue).to(userExchange).with("user.registered");
    }

    @Bean
    public Binding orderBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with("order.created");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}


