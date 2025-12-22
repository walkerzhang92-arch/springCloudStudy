package com.walker.orderservice.service.impl;

import com.walker.orderservice.common.ApiResult;
import com.walker.orderservice.dto.CourseDTO;
import com.walker.orderservice.dto.CreateOrderResponse;
import com.walker.orderservice.dto.OrderStatusResponse;
import com.walker.orderservice.entity.TOrder;
import com.walker.orderservice.enums.OrderStatus;
import com.walker.orderservice.feign.CourseFeignClient;
import com.walker.orderservice.mapper.OrderMapper;
import com.walker.orderservice.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final CourseFeignClient courseFeignClient;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(CourseFeignClient courseFeignClient, OrderMapper orderMapper) {
        this.courseFeignClient = courseFeignClient;
        this.orderMapper = orderMapper;
    }

    @Override
    public CreateOrderResponse createOrder(Long userId, Long courseId) {
        if (userId == null || userId <= 0) throw new IllegalArgumentException("userId 非法");
        if (courseId == null || courseId <= 0) throw new IllegalArgumentException("courseId 非法");

        // 1) 通过 Feign 拿课程信息（价格必须以 course-service 为准）
        ApiResult<CourseDTO> resp = courseFeignClient.getCourse(courseId);
        if (resp == null || resp.getCode() != 0 || resp.getData() == null) {
            throw new RuntimeException("课程服务不可用或课程不存在");
        }

        CourseDTO course = resp.getData();
        if (course.getStock() == null || course.getStock() <= 0) {
            throw new RuntimeException("库存不足");
        }

        // 2) 创建订单（amount = price）
        TOrder order = new TOrder();
        order.setUserId(userId);
        order.setCourseId(courseId);
        order.setAmount(course.getPrice());
        order.setStatus("NEW");

        orderMapper.insert(order);

        return new CreateOrderResponse(order.getId(), order.getStatus(), order.getAmount());
    }

    @Override
    public OrderStatusResponse pay(Long orderId) {
        if (orderId == null || orderId <= 0) throw new IllegalArgumentException("orderId 非法");

        // NEW -> PAID（CAS）
        int updated = orderMapper.updateStatusCas(orderId, OrderStatus.NEW.name(), OrderStatus.PAID.name());
        if (updated == 0) {
            // 更新失败：要么订单不存在，要么状态不是 NEW
            TOrder current = orderMapper.selectById(orderId);
            if (current == null) throw new RuntimeException("订单不存在: " + orderId);
            throw new RuntimeException("订单状态不允许支付，当前状态=" + current.getStatus());
        }

        return new OrderStatusResponse(orderId, OrderStatus.PAID.name());
    }

    @Override
    public OrderStatusResponse cancel(Long orderId) {
        if (orderId == null || orderId <= 0) throw new IllegalArgumentException("orderId 非法");

        // NEW -> CLOSED（CAS）
        int updated = orderMapper.updateStatusCas(orderId, OrderStatus.NEW.name(), OrderStatus.CLOSED.name());
        if (updated == 0) {
            TOrder current = orderMapper.selectById(orderId);
            if (current == null) throw new RuntimeException("订单不存在: " + orderId);
            throw new RuntimeException("订单状态不允许取消，当前状态=" + current.getStatus());
        }

        return new OrderStatusResponse(orderId, OrderStatus.CLOSED.name());
    }

    @Override
    public OrderStatusResponse getStatus(Long orderId) {
        if (orderId == null || orderId <= 0) throw new IllegalArgumentException("orderId 非法");

        TOrder current = orderMapper.selectById(orderId);
        if (current == null) throw new RuntimeException("订单不存在: " + orderId);

        return new OrderStatusResponse(orderId, current.getStatus());
    }

}
