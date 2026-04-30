package com.trust.quant.trade.infrastructure.rpc.dto;

import com.trust.common.core.api.BaseRequest;

public class LatestPriceReq extends BaseRequest {
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
