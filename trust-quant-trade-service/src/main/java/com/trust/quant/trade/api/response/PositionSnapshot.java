package com.trust.quant.trade.api.response;

import java.math.BigDecimal;

public class PositionSnapshot {
    private String accountId;
    private String symbol;
    private BigDecimal quantity;

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
