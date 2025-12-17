package com.walker.orderservice.controller;

import com.walker.orderservice.mapper.OrderMapper;
import com.walker.orderservice.model.Order;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private final OrderMapper orderMapper;

    public OrderController(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @PostMapping("/order")
    public Order createOrder(@RequestParam Long courseId,
                             @RequestHeader(value = "X-USER-ID", required = false) String userIdFromGateway) {

        // 这里先用网关注入的 X-USER-ID；如果没走网关就默认 1
        Long userId = (userIdFromGateway == null || userIdFromGateway.isBlank())
                ? 1L : Long.parseLong(userIdFromGateway);

        // 先简单固定金额，后面从 course-service 查课程价格
        Order order = new Order();
        order.setUserId(userId);
        order.setCourseId(courseId);
        order.setAmount(19900);
        order.setStatus("NEW");

        orderMapper.insert(order);
        return order;
    }

    @GetMapping("/order/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderMapper.findById(id);
    }
}
