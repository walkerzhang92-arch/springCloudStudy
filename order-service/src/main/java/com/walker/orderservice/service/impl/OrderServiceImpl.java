package com.walker.orderservice.service.impl;

import com.walker.orderservice.common.ApiResult;
import com.walker.orderservice.dto.CourseDTO;
import com.walker.orderservice.dto.CreateOrderResponse;
import com.walker.orderservice.dto.OrderStatusResponse;
import com.walker.orderservice.entity.TOrder;
import com.walker.orderservice.enums.OrderStatus;
import com.walker.orderservice.feign.CourseFeignClient;
import com.walker.orderservice.mapper.OrderEventMapper;
import com.walker.orderservice.mapper.OrderMapper;
import com.walker.orderservice.service.OrderService;
import com.walker.orderservice.service.domain.OrderStateMachine;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final CourseFeignClient courseFeignClient;
    private final OrderMapper orderMapper;
    private final OrderEventMapper orderEventMapper;

    public OrderServiceImpl(CourseFeignClient courseFeignClient, OrderMapper orderMapper, OrderEventMapper orderEventMapper) {
        this.courseFeignClient = courseFeignClient;
        this.orderMapper = orderMapper;
        this.orderEventMapper = orderEventMapper;
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
        return transitionWithIdempotency(orderId, OrderStatus.NEW, OrderStatus.PAID, "pay");
    }

    @Override
    public OrderStatusResponse cancel(Long orderId) {
        return transitionWithIdempotency(orderId, OrderStatus.NEW, OrderStatus.CLOSED, "cancel");
    }

    @Override
    public OrderStatusResponse getStatus(Long orderId) {
        if (orderId == null || orderId <= 0) throw new IllegalArgumentException("orderId 非法");

        TOrder current = orderMapper.selectById(orderId);
        if (current == null) throw new RuntimeException("订单不存在: " + orderId);

        return new OrderStatusResponse(orderId, current.getStatus());
    }

    /**
     * 工程化核心方法：
     * 1) 状态机校验
     * 2) CAS 更新（数据库保证并发互斥）
     * 3) CAS 失败后按当前状态做幂等/拒绝/异常处理
     */
    private OrderStatusResponse transitionWithIdempotency(Long orderId,
                                                          OrderStatus from,
                                                          OrderStatus to,
                                                          String action) {
        if (orderId == null || orderId <= 0) throw new IllegalArgumentException("orderId 非法");

        // 1) 状态机规则（防止代码写错）
        OrderStateMachine.assertCanTransfer(from, to);

        // 2) CAS：数据库原子互斥
        int updated = orderMapper.updateStatusCas(orderId, from.name(), to.name());
        if (updated == 1) {
            return new OrderStatusResponse(orderId, to.name());
        }

        // 3) CAS 失败：查当前状态，做工程化处理（幂等/拒绝/异常）
        String current = orderMapper.selectStatus(orderId);
        if (current == null) throw new RuntimeException("订单不存在: " + orderId);

        // 3.1 幂等：已经是目标状态，直接返回成功
        if (to.name().equals(current)) {
            return new OrderStatusResponse(orderId, current);
        }

        // 3.2 如果已经是终态：明确拒绝（并发下最常见：pay vs close）
        if (OrderStatus.CLOSED.name().equals(current) && to == OrderStatus.PAID) {
            // 支付晚到：工程上应进入退款/异常单（这里先抛清晰异常）
            throw new RuntimeException("支付失败：订单已关闭（可能超时关闭），orderId=" + orderId);
        }

        if (OrderStatus.PAID.name().equals(current) && to == OrderStatus.CLOSED) {
            // 已支付不能取消/关单
            throw new RuntimeException("取消失败：订单已支付，orderId=" + orderId);
        }

        // 支付晚到：订单已关闭 -> 写入补偿事件（Outbox）
        if (OrderStatus.CLOSED.name().equals(current)) {
            String payload = "{\"orderId\":" + orderId + ",\"reason\":\"PAY_LATE\"}";
            // 插入失败（例如唯一键冲突）也没关系：说明事件已经存在
            try {
                orderEventMapper.insertNew(orderId, "PAY_LATE_REFUND", payload);
            } catch (Exception ignored) {
                // 事件已存在，忽略
            }
            throw new RuntimeException("支付晚到：订单已关闭，已记录退款事件，orderId=" + orderId);
        }

        // 其他状态：统一拒绝
        throw new RuntimeException("操作失败(" + action + ")：当前状态=" + current + "，不允许从 " + from + " -> " + to);
    }

}
