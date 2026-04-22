package com.trust.quant.trade.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {
    private long tradeId;
    private long buyOrderId;
    private long sellOrderId;
    private String buyAccountId;
    private String sellAccountId;
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;
    private LocalDateTime tradeTime;

    public long getTradeId() { return tradeId; }
    public void setTradeId(long tradeId) { this.tradeId = tradeId; }
    public long getBuyOrderId() { return buyOrderId; }
    public void setBuyOrderId(long buyOrderId) { this.buyOrderId = buyOrderId; }
    public long getSellOrderId() { return sellOrderId; }
    public void setSellOrderId(long sellOrderId) { this.sellOrderId = sellOrderId; }
    public String getBuyAccountId() { return buyAccountId; }
    public void setBuyAccountId(String buyAccountId) { this.buyAccountId = buyAccountId; }
    public String getSellAccountId() { return sellAccountId; }
    public void setSellAccountId(String sellAccountId) { this.sellAccountId = sellAccountId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public LocalDateTime getTradeTime() { return tradeTime; }
    public void setTradeTime(LocalDateTime tradeTime) { this.tradeTime = tradeTime; }
}
