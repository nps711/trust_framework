package com.trust.quant.quotation.api.response;

public class LimitPriceRes {
    private TickSnapshot tick;

    public TickSnapshot getTick() {
        return tick;
    }

    public void setTick(TickSnapshot tick) {
        this.tick = tick;
    }
}
