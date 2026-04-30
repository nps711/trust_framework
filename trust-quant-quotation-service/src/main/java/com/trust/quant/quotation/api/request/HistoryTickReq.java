package com.trust.quant.quotation.api.request;

import com.trust.common.core.api.BaseRequest;
import jakarta.validation.constraints.NotBlank;

public class HistoryTickReq extends BaseRequest {
    @NotBlank
    private String symbol;
    private int limit = 20;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
