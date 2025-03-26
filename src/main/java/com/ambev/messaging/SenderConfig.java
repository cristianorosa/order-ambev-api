package com.ambev.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SenderConfig {

    @Value("${queue.name.sender}")
    private String message;
    
    @Bean
    Queue queue() {
        return new Queue(message, true);
    }
}