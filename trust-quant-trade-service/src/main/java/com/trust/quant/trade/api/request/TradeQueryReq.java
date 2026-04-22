package com.trust.quant.trade.api.request;

import com.trust.common.core.api.BaseRequest;
import jakarta.validation.constraints.NotBlank;

public class TradeQueryReq extends BaseRequest {
    @NotBlank
    private String accountId;

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
}
