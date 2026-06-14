package com.hotelreservation.reservation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RESERVATION_EXCHANGE = "reservation.exchange";
    public static final String RESERVATION_CREATED_QUEUE = "reservation.created.queue";
    public static final String RESERVATION_CREATED_ROUTING_KEY = "reservation.created";

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(RESERVATION_EXCHANGE);
    }

    @Bean
    public Queue reservationCreatedQueue() {
        return QueueBuilder.durable(RESERVATION_CREATED_QUEUE).build();
    }

    @Bean
    public Binding reservationCreatedBinding() {
        return BindingBuilder
                .bind(reservationCreatedQueue())
                .to(reservationExchange())
                .with(RESERVATION_CREATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
