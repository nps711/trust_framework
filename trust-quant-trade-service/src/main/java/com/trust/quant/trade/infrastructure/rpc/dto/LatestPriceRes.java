package com.trust.quant.trade.infrastructure.rpc.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LatestPriceRes {
    private TickSnapshot tick;

    public TickSnapshot getTick() {
        return tick;
    }

    public void setTick(TickSnapshot tick) {
        this.tick = tick;
    }

    public static class TickSnapshot {
        private String symbol;
        private BigDecimal latestPrice;
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
}
