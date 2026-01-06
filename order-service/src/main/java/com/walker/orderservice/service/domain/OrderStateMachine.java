package com.walker.orderservice.service.domain;

import com.walker.orderservice.enums.OrderStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class OrderStateMachine {
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOW = new EnumMap<>(OrderStatus.class);

    static {
        ALLOW.put(OrderStatus.NEW, EnumSet.of(OrderStatus.PAID, OrderStatus.CLOSED));
        ALLOW.put(OrderStatus.PAID, EnumSet.noneOf(OrderStatus.class));
        ALLOW.put(OrderStatus.CLOSED, EnumSet.noneOf(OrderStatus.class));
    }

    private OrderStateMachine() {}

    public static void assertCanTransfer(OrderStatus from, OrderStatus to) {
        Set<OrderStatus> tos = ALLOW.getOrDefault(from, EnumSet.noneOf(OrderStatus.class));
        if (!tos.contains(to)) {
            throw new IllegalStateException("不允许状态流转: " + from + " -> " + to);
        }
    }
}
