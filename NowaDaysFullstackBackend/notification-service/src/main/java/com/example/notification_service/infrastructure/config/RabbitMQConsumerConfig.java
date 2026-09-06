package com.example.notification_service.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConsumerConfig {
    public static final String USER_QUEUE = "notification.user.registered.queue";
    public static final String ORDER_QUEUE = "notification.order.created.queue";
    public static final String DLX_NAME = "notification.dlx";
    public static final String DLQ_NAME = "notification.dead-letter.queue";
    public static final String DLQ_ROUTING_KEY = "notification.dead-letter";

    @Bean
    public DirectExchange deadLetterExchange(){
        return new DirectExchange(DLX_NAME);
    }
    @Bean
    public Queue deadLetterQueue(){
        return new Queue(DLQ_NAME,true);
    }
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue,DirectExchange deadLetterExchange){
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(DLQ_ROUTING_KEY);
    }
    @Bean
    public Queue userRegisteredQueue() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",DLX_NAME);
        args.put("x-dead-letter-routing-key",DLQ_ROUTING_KEY);

        return new Queue(USER_QUEUE, true,false,false,args);
    }

    @Bean
    public Queue orderCreatedQueue() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",DLX_NAME);
        args.put("x-dead-letter-routing-key",DLQ_ROUTING_KEY);
        return new Queue(ORDER_QUEUE, true,false,false,args);
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


