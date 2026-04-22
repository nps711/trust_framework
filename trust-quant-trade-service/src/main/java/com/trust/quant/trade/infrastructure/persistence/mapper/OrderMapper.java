package com.trust.quant.trade.infrastructure.persistence.mapper;

import com.trust.quant.trade.domain.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("""
            INSERT INTO qt_order (
                id, request_id, account_id, symbol, side, order_type, tif,
                price, quantity, remaining_quantity, status, create_time, update_time
            ) VALUES (
                #{orderId}, #{requestId}, #{accountId}, #{symbol}, #{side}, #{orderType}, #{timeInForce},
                #{price}, #{quantity}, #{remainingQuantity}, #{status}, #{createTime}, #{updateTime}
            )
            """)
    int insert(Order order);

    @Update("""
            UPDATE qt_order
            SET remaining_quantity = #{remainingQuantity}, status = #{status}, update_time = #{updateTime}
            WHERE id = #{orderId}
            """)
    int updateState(Order order);

    @Update("""
            UPDATE qt_order
            SET status = 'CANCELED', update_time = #{updateTime}
            WHERE id = #{orderId}
              AND account_id = #{accountId}
              AND status IN ('NEW','PARTIALLY_FILLED')
            """)
    int cancelByIdAndAccount(@Param("orderId") Long orderId,
                             @Param("accountId") String accountId,
                             @Param("updateTime") LocalDateTime updateTime);

    @Select("""
            SELECT id, request_id, account_id, symbol, side, order_type, tif,
                   price, quantity, remaining_quantity, status, create_time, update_time
            FROM qt_order
            WHERE id = #{orderId}
            """)
    @Results(id = "orderResult", value = {
            @Result(column = "id", property = "orderId"),
            @Result(column = "request_id", property = "requestId"),
            @Result(column = "account_id", property = "accountId"),
            @Result(column = "order_type", property = "orderType"),
            @Result(column = "tif", property = "timeInForce"),
            @Result(column = "remaining_quantity", property = "remainingQuantity"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime")
    })
    Order findById(@Param("orderId") Long orderId);

    @Select("""
            SELECT id, request_id, account_id, symbol, side, order_type, tif,
                   price, quantity, remaining_quantity, status, create_time, update_time
            FROM qt_order
            WHERE account_id = #{accountId}
            ORDER BY create_time DESC
            """)
    @Results(id = "orderListResult", value = {
            @Result(column = "id", property = "orderId"),
            @Result(column = "request_id", property = "requestId"),
            @Result(column = "account_id", property = "accountId"),
            @Result(column = "order_type", property = "orderType"),
            @Result(column = "tif", property = "timeInForce"),
            @Result(column = "remaining_quantity", property = "remainingQuantity"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime")
    })
    List<Order> findByAccountId(@Param("accountId") String accountId);
}
