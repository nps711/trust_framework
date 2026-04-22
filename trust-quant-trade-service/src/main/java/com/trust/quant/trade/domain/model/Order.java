package com.trust.quant.trade.domain.model;

import com.trust.quant.trade.domain.type.OrderSide;
import com.trust.quant.trade.domain.type.OrderStatus;
import com.trust.quant.trade.domain.type.OrderType;
import com.trust.quant.trade.domain.type.TimeInForce;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private long orderId;
    private String requestId;
    private String accountId;
    private String symbol;
    private OrderSide side;
    private OrderType orderType;
    private TimeInForce timeInForce;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal remainingQuantity;
    private OrderStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public OrderSide getSide() { return side; }
    public void setSide(OrderSide side) { this.side = side; }
    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }
    public TimeInForce getTimeInForce() { return timeInForce; }
    public void setTimeInForce(TimeInForce timeInForce) { this.timeInForce = timeInForce; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(BigDecimal remainingQuantity) { this.remainingQuantity = remainingQuantity; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
