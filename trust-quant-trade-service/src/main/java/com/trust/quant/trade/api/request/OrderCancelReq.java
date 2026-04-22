package com.trust.quant.trade.api.request;

import com.trust.common.core.api.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderCancelReq extends BaseRequest {
    @NotNull
    private Long orderId;
    @NotBlank
    private String accountId;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
}
