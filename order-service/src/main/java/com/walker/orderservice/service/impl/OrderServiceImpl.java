package com.walker.orderservice.service.impl;

import com.walker.orderservice.common.ApiResult;
import com.walker.orderservice.dto.CourseDTO;
import com.walker.orderservice.dto.CreateOrderResponse;
import com.walker.orderservice.entity.TOrder;
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
}
