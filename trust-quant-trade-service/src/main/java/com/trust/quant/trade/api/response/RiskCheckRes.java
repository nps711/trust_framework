package com.trust.quant.trade.api.response;

public class RiskCheckRes {
    private boolean passed;
    private String reason;

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
