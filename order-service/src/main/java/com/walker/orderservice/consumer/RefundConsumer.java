package com.walker.orderservice.consumer;

import com.walker.orderservice.config.RefundMqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RefundConsumer {

    @RabbitListener(queues = RefundMqConfig.REFUND_QUEUE)
    public void onRefundMessage(String payload) {
        // 先做 mock：打印出来证明“消息确实被消费到了”
        System.out.println("[RefundConsumer] refund requested: " + payload);

        // TODO 下一步：这里再写“退款业务”（调用第三方退款接口/写退款单/幂等控制）
    }
}
