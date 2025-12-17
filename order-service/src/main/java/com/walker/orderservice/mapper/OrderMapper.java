package com.walker.orderservice.mapper;

import com.walker.orderservice.model.Order;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {

    @Insert("""
        INSERT INTO t_order(user_id, course_id, amount, status)
        VALUES(#{userId}, #{courseId}, #{amount}, #{status})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("""
        SELECT id, user_id, course_id, amount, status, created_at
        FROM t_order
        WHERE id = #{id}
    """)
    Order findById(@Param("id") Long id);
}
