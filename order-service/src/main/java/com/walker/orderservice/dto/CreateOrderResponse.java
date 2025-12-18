package com.walker.orderservice.dto;

public class CreateOrderResponse {
    private Long orderId;
    private String status;
    private Integer amount;

    public CreateOrderResponse() {}

    public CreateOrderResponse(Long orderId, String status, Integer amount) {
        this.orderId = orderId;
        this.status = status;
        this.amount = amount;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
}
