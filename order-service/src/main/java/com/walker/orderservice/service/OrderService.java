package com.walker.orderservice.service;

import com.walker.orderservice.dto.CreateOrderResponse;

public interface OrderService {
    CreateOrderResponse createOrder(Long userId, Long courseId);
}