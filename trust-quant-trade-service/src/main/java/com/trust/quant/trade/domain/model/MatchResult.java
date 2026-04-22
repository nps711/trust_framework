package com.trust.quant.trade.domain.model;

import java.util.List;

public class MatchResult {
    private final List<Trade> trades;

    public MatchResult(List<Trade> trades) {
        this.trades = trades;
    }

    public List<Trade> getTrades() {
        return trades;
    }
}
