package com.trust.quant.quotation.api.response;

import java.util.List;

public class HistoryTickRes {
    private List<TickSnapshot> ticks;

    public List<TickSnapshot> getTicks() {
        return ticks;
    }

    public void setTicks(List<TickSnapshot> ticks) {
        this.ticks = ticks;
    }
}
