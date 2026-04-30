package com.trust.quant.quotation.api.response;

public class LatestPriceRes {
    private TickSnapshot tick;

    public TickSnapshot getTick() {
        return tick;
    }

    public void setTick(TickSnapshot tick) {
        this.tick = tick;
    }
}
