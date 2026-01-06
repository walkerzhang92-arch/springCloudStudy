package com.walker.orderservice.job;

import com.walker.orderservice.config.RefundMqConfig;
import com.walker.orderservice.dto.OrderEventRow;
import com.walker.orderservice.mapper.OrderEventMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RefundOutboxJob {

    private final OrderEventMapper orderEventMapper;
    private final RabbitTemplate rabbitTemplate;

    public RefundOutboxJob(OrderEventMapper orderEventMapper, RabbitTemplate rabbitTemplate) {
        this.orderEventMapper = orderEventMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @XxlJob("publishRefundEvents")
    public ReturnT<String> publishRefundEvents() {
        int limit = 50;
        List<OrderEventRow> rows = orderEventMapper.pickToSend(limit);

        int ok = 0, fail = 0;
        for (OrderEventRow row : rows) {
            try {
                // 只处理退款事件
                if (!"PAY_LATE_REFUND".equals(row.getEventType())) continue;

                rabbitTemplate.convertAndSend(
                        RefundMqConfig.REFUND_EXCHANGE,
                        RefundMqConfig.REFUND_KEY,
                        row.getPayload()
                );

                orderEventMapper.markSent(row.getId());
                ok++;
            } catch (Exception e) {
                int retry = row.getRetryCount() == null ? 0 : row.getRetryCount();
                int nextDelay = Math.min(300, (int) Math.pow(2, retry) * 5); // 5,10,20,40... 最大 300s
                orderEventMapper.markFailed(row.getId(), nextDelay);
                fail++;
                XxlJobHelper.log("send failed, eventId={}, retry={}, err={}", row.getId(), retry, e.getMessage());
            }
        }

        XxlJobHelper.log("publishRefundEvents done, picked={}, ok={}, fail={}", rows.size(), ok, fail);
        return ReturnT.SUCCESS;
    }
}
