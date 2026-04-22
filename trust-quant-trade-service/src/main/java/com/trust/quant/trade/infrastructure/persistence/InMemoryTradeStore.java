package com.trust.quant.trade.infrastructure.persistence;

import com.trust.quant.trade.domain.model.Trade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class InMemoryTradeStore {
    private final List<Trade> trades = new CopyOnWriteArrayList<>();

    public void saveAll(List<Trade> executedTrades) {
        trades.addAll(executedTrades);
    }

    public List<Trade> listByAccount(String accountId) {
        return trades.stream()
                .filter(t -> accountId.equals(t.getBuyAccountId()) || accountId.equals(t.getSellAccountId()))
                .sorted(Comparator.comparing(Trade::getTradeTime).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
