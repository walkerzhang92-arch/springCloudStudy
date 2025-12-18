package com.walker.orderservice.mapper;

import com.walker.orderservice.entity.TOrder;
import com.walker.orderservice.model.Order;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {

    @Insert("""
        INSERT INTO t_order(user_id, course_id, amount, status, created_at)
        VALUES (#{userId}, #{courseId}, #{amount}, #{status}, NOW())
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TOrder order);

    @Select("""
        SELECT id,
               user_id AS userId,
               course_id AS courseId,
               amount,
               status,
               created_at AS createdAt
        FROM t_order
        WHERE id = #{id}
        """)
    TOrder selectById(@Param("id") Long id);
}
