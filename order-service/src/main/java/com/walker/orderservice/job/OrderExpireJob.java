package com.walker.orderservice.job;

import com.walker.orderservice.mapper.OrderMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;




@Component
public class OrderExpireJob {

    private final OrderMapper orderMapper;

    @Value("${order.expire-minutes:15}")
    private int defaultExpireMinutes;

    public OrderExpireJob(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    // XXL-JOB 后台 JobHandler 填：closeExpiredOrders
    @XxlJob("closeExpiredOrders")
    public ReturnT<String> closeExpiredOrders() {

        int minutes = defaultExpireMinutes;
        String param = XxlJobHelper.getJobParam();  // 后台“任务参数”
        if (param != null && !param.isBlank()) {
            try {
                minutes = Integer.parseInt(param.trim());
            } catch (Exception e) {
                XxlJobHelper.log("Invalid param minutes={}, fallback to default={}", param, defaultExpireMinutes);
            }
        }

        int affected = orderMapper.closeExpiredOrders(minutes);
        XxlJobHelper.log("closeExpiredOrders OK, minutes={}, affected={}", minutes, affected);

        return ReturnT.SUCCESS;
    }
}
