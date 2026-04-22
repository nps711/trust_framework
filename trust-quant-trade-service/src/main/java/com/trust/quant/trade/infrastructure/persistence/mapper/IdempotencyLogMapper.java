package com.trust.quant.trade.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface IdempotencyLogMapper {

    @Insert("""
            INSERT INTO qt_idempotency_log (id, request_id, biz_type, create_time)
            VALUES (#{id}, #{requestId}, #{bizType}, #{createTime})
            """)
    int insert(@Param("id") Long id,
               @Param("requestId") String requestId,
               @Param("bizType") String bizType,
               @Param("createTime") LocalDateTime createTime);
}
