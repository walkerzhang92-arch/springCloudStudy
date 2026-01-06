package com.walker.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefundMqConfig {

    public static final String REFUND_EXCHANGE = "refund.exchange";
    public static final String REFUND_QUEUE = "refund.queue";
    public static final String REFUND_KEY = "refund.request";

    @Bean
    public DirectExchange refundExchange() {
        return new DirectExchange(REFUND_EXCHANGE, true, false);
    }

    @Bean
    public Queue refundQueue() {
        return QueueBuilder.durable(REFUND_QUEUE).build();
    }

    @Bean
    public Binding refundBinding(DirectExchange refundExchange, Queue refundQueue) {
        return BindingBuilder.bind(refundQueue).to(refundExchange).with(REFUND_KEY);
    }
}
