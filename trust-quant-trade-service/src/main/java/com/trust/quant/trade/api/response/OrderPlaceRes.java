package com.trust.quant.trade.api.response;

import com.trust.quant.trade.domain.type.OrderStatus;

public class OrderPlaceRes {
    private Long orderId;
    private OrderStatus status;
    private String message;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
