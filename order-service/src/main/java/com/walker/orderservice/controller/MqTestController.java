package com.walker.orderservice.controller;

import com.walker.orderservice.config.RefundMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/mq")
public class MqTestController {

    private final RabbitTemplate rabbitTemplate;

    public MqTestController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/refund-test")
    public String send() {
        rabbitTemplate.convertAndSend(
                RefundMqConfig.REFUND_EXCHANGE,
                RefundMqConfig.REFUND_KEY,
                "{\"orderId\":123,\"reason\":\"TEST\"}"
        );
        return "ok";
    }
}
