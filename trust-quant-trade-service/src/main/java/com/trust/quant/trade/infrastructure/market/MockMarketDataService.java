package com.trust.quant.trade.infrastructure.market;

import com.trust.quant.trade.domain.model.MarketTick;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockMarketDataService {
    private final Map<String, MarketTick> ticks = new ConcurrentHashMap<>();

    public void pushTick(String symbol, BigDecimal lastPrice) {
        MarketTick tick = new MarketTick();
        tick.setSymbol(symbol);
        tick.setLastPrice(lastPrice);
        tick.setUpdateTime(LocalDateTime.now());
        ticks.put(symbol, tick);
    }

    public Optional<MarketTick> latest(String symbol) {
        return Optional.ofNullable(ticks.get(symbol));
    }
}
