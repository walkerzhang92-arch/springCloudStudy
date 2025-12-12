package com.walker.orderservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable Long id) {
        return "Order-" + id;
    }

    @PostMapping("/order")
    public String createOrder(@RequestParam Long userId,
                              @RequestParam Long courseId) {
        return "创建订单成功, userId=" + userId + ", courseId=" + courseId;
    }
}
