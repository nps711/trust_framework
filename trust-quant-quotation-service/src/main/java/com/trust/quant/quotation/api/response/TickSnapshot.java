package com.trust.quant.quotation.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TickSnapshot {
    private String symbol;
    private BigDecimal latestPrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal preClosePrice;
    private BigDecimal upLimitPrice;
    private BigDecimal downLimitPrice;
    private LocalDateTime updateTime;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(BigDecimal latestPrice) {
        this.latestPrice = latestPrice;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(BigDecimal preClosePrice) {
        this.preClosePrice = preClosePrice;
    }

    public BigDecimal getUpLimitPrice() {
        return upLimitPrice;
    }

    public void setUpLimitPrice(BigDecimal upLimitPrice) {
        this.upLimitPrice = upLimitPrice;
    }

    public BigDecimal getDownLimitPrice() {
        return downLimitPrice;
    }

    public void setDownLimitPrice(BigDecimal downLimitPrice) {
        this.downLimitPrice = downLimitPrice;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
