package com.trust.quant.trade.domain.model;

public class RiskDecision {
    private final boolean passed;
    private final String reason;

    public RiskDecision(boolean passed, String reason) {
        this.passed = passed;
        this.reason = reason;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getReason() {
        return reason;
    }
}
