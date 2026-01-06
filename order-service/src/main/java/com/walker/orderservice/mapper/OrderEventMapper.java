package com.walker.orderservice.mapper;

import com.walker.orderservice.dto.OrderEventRow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderEventMapper {

    @Insert("""
        INSERT INTO t_order_event(order_id, event_type, payload, status, retry_count, next_retry_at, created_at)
        VALUES (#{orderId}, #{eventType}, #{payload}, 'NEW', 0, NOW(), NOW())
        """)
    int insertNew(@Param("orderId") Long orderId,
                  @Param("eventType") String eventType,
                  @Param("payload") String payload);

    @Select("""
        SELECT id, order_id, event_type, payload, status, retry_count
        FROM t_order_event
        WHERE status IN ('NEW','FAILED')
          AND next_retry_at <= NOW()
        ORDER BY id ASC
        LIMIT #{limit}
        """)
    List<OrderEventRow> pickToSend(@Param("limit") int limit);

    @Update("""
        UPDATE t_order_event
        SET status='SENT', sent_at=NOW()
        WHERE id=#{id} AND status IN ('NEW','FAILED')
        """)
    int markSent(@Param("id") Long id);

    @Update("""
        UPDATE t_order_event
        SET status='FAILED',
            retry_count = retry_count + 1,
            next_retry_at = DATE_ADD(NOW(), INTERVAL #{nextDelaySeconds} SECOND)
        WHERE id=#{id}
        """)
    int markFailed(@Param("id") Long id, @Param("nextDelaySeconds") int nextDelaySeconds);

}
