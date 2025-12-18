package com.walker.orderservice.controller;

import com.walker.orderservice.common.ApiResult;
import com.walker.orderservice.dto.CreateOrderResponse;
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
    public ApiResult<CreateOrderResponse> create(@RequestParam("courseId") Long courseId) {
        Long userId = 1L; // TODO：下一步接网关透传 header 或 JWT 解析
        return ApiResult.success(orderService.createOrder(userId, courseId));
    }
}
