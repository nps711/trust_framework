package com.trust.quant.trade.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarketTick {
    private String symbol;
    private BigDecimal lastPrice;
    private LocalDateTime updateTime;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getLastPrice() { return lastPrice; }
    public void setLastPrice(BigDecimal lastPrice) { this.lastPrice = lastPrice; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
