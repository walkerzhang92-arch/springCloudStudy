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

    @Update("""
        UPDATE t_order
        SET status = #{toStatus}
        WHERE id = #{id}
          AND status = #{fromStatus}
        """)
    int updateStatusCas(@Param("id") Long id,
                        @Param("fromStatus") String fromStatus,
                        @Param("toStatus") String toStatus);


    @Update("""
        UPDATE t_order
        SET status = 'CLOSED'
        WHERE status = 'NEW'
        AND created_at < DATE_SUB(NOW(), INTERVAL #{minutes} MINUTE)
    """)
    int closeExpiredOrders(@Param("minutes") int minutes);
}
