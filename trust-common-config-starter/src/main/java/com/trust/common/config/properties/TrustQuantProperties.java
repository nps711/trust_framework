package com.trust.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "trust.quant")
public class TrustQuantProperties {
    private BigDecimal maxNotional = new BigDecimal("1000000");
    private boolean mockMarketEnabled = true;

    public BigDecimal getMaxNotional() {
        return maxNotional;
    }

    public void setMaxNotional(BigDecimal maxNotional) {
        this.maxNotional = maxNotional;
    }

    public boolean isMockMarketEnabled() {
        return mockMarketEnabled;
    }

    public void setMockMarketEnabled(boolean mockMarketEnabled) {
        this.mockMarketEnabled = mockMarketEnabled;
    }
}
