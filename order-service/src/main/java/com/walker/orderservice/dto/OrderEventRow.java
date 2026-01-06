package com.walker.orderservice.dto;

public class OrderEventRow {
    public Long id;
    public Long order_id;
    public String event_type;
    public String payload;
    public String status;
    public Integer retry_count;

    public Long getId() { return id; }
    public Long getOrderId() { return order_id; }
    public String getEventType() { return event_type; }
    public String getPayload() { return payload; }
    public Integer getRetryCount() { return retry_count; }
}
