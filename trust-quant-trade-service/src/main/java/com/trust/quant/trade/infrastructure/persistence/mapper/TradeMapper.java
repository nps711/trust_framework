package com.trust.quant.trade.infrastructure.persistence.mapper;

import com.trust.quant.trade.domain.model.Trade;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TradeMapper {

    @Insert("""
            INSERT INTO qt_trade (
                id, buy_order_id, sell_order_id, buy_account_id, sell_account_id,
                symbol, price, quantity, trade_time
            ) VALUES (
                #{tradeId}, #{buyOrderId}, #{sellOrderId}, #{buyAccountId}, #{sellAccountId},
                #{symbol}, #{price}, #{quantity}, #{tradeTime}
            )
            """)
    int insert(Trade trade);

    @Select("""
            SELECT id, buy_order_id, sell_order_id, buy_account_id, sell_account_id,
                   symbol, price, quantity, trade_time
            FROM qt_trade
            WHERE buy_account_id = #{accountId} OR sell_account_id = #{accountId}
            ORDER BY trade_time DESC
            """)
    @Results(id = "tradeResult", value = {
            @Result(column = "id", property = "tradeId"),
            @Result(column = "buy_order_id", property = "buyOrderId"),
            @Result(column = "sell_order_id", property = "sellOrderId"),
            @Result(column = "buy_account_id", property = "buyAccountId"),
            @Result(column = "sell_account_id", property = "sellAccountId"),
            @Result(column = "trade_time", property = "tradeTime")
    })
    List<Trade> findByAccountId(@Param("accountId") String accountId);
}
