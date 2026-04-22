package com.trust.quant.trade.api.request;

import com.trust.common.core.api.BaseRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MarketTickReq extends BaseRequest {
    @NotBlank
    private String symbol;
    @NotNull
    @DecimalMin("0.000001")
    private BigDecimal lastPrice;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getLastPrice() { return lastPrice; }
    public void setLastPrice(BigDecimal lastPrice) { this.lastPrice = lastPrice; }
}
