package com.walker.orderservice.controller;

import com.walker.orderservice.common.ApiResult;
import com.walker.orderservice.dto.CreateOrderResponse;
import com.walker.orderservice.dto.OrderStatusResponse;
import com.walker.orderservice.mapper.OrderMapper;
import com.walker.orderservice.model.Order;
import com.walker.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ApiResult<CreateOrderResponse> create(@RequestHeader("X-USER-ID") String userIdStr,
                                                 @RequestParam("courseId") Long courseId) {

        Long userId = Long.valueOf(userIdStr);
        return ApiResult.success(orderService.createOrder(userId, courseId));
    }

    @PostMapping("/pay/{orderId}")
    public ApiResult<OrderStatusResponse> pay(@PathVariable Long orderId) {
        return ApiResult.success(orderService.pay(orderId));
    }

    @PostMapping("/cancel/{orderId}")
    public ApiResult<OrderStatusResponse> cancel(@PathVariable Long orderId) {
        return ApiResult.success(orderService.cancel(orderId));
    }

    @GetMapping("/{orderId}/status")
    public ApiResult<OrderStatusResponse> status(@PathVariable Long orderId) {
        return ApiResult.success(orderService.getStatus(orderId));
    }
}
