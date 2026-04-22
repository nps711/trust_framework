package com.trust.quant.trade.api.request;

import com.trust.common.core.api.BaseRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RiskCheckReq extends BaseRequest {
    @NotBlank
    private String accountId;
    @NotBlank
    private String symbol;
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
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
