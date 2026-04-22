package com.trust.quant.trade.api.request;

import com.trust.common.core.api.BaseRequest;
import com.trust.quant.trade.domain.type.OrderSide;
import com.trust.quant.trade.domain.type.OrderType;
import com.trust.quant.trade.domain.type.TimeInForce;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OrderPlaceReq extends BaseRequest {
    @NotBlank
    private String accountId;
    @NotBlank
    private String symbol;
    @NotNull
    private OrderSide side;
    @NotNull
    private OrderType orderType = OrderType.LIMIT;
    @NotNull
    private TimeInForce timeInForce = TimeInForce.GTC;
    @NotNull
    @DecimalMin("0.000001")
    private BigDecimal price;
    @NotNull
    @DecimalMin("0.000001")
    private BigDecimal quantity;

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
}
