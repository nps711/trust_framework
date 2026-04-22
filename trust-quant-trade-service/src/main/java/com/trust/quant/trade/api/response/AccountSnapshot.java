package com.trust.quant.trade.api.response;

import java.math.BigDecimal;

public class AccountSnapshot {
    private String accountId;
    private BigDecimal availableBalance;

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
}
