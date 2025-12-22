package com.walker.orderservice.service;

import com.walker.orderservice.dto.CreateOrderResponse;
import com.walker.orderservice.dto.OrderStatusResponse;

public interface OrderService {
    CreateOrderResponse createOrder(Long userId, Long courseId);

    OrderStatusResponse pay(Long orderId);

    OrderStatusResponse cancel(Long orderId);

    OrderStatusResponse getStatus(Long orderId);
}