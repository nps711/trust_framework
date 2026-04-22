package com.trust.quant.trade.infrastructure.persistence;

import com.trust.quant.trade.api.response.AccountSnapshot;
import com.trust.quant.trade.api.response.PositionSnapshot;
import com.trust.quant.trade.domain.model.Trade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryLedger {
    private final Map<String, BigDecimal> balanceMap = new HashMap<>();
    private final Map<String, Map<String, BigDecimal>> positionMap = new HashMap<>();

    public synchronized BigDecimal balanceOf(String accountId) {
        return balanceMap.computeIfAbsent(accountId, k -> new BigDecimal("1000000.000000"));
    }

    public synchronized void settle(List<Trade> trades) {
        for (Trade trade : trades) {
            BigDecimal notional = trade.getPrice().multiply(trade.getQuantity()).setScale(6, RoundingMode.HALF_UP);
            balanceMap.put(trade.getBuyAccountId(), balanceOf(trade.getBuyAccountId()).subtract(notional));
            balanceMap.put(trade.getSellAccountId(), balanceOf(trade.getSellAccountId()).add(notional));

            addPosition(trade.getBuyAccountId(), trade.getSymbol(), trade.getQuantity());
            addPosition(trade.getSellAccountId(), trade.getSymbol(), trade.getQuantity().negate());
        }
    }

    public synchronized AccountSnapshot accountSnapshot(String accountId) {
        AccountSnapshot snapshot = new AccountSnapshot();
        snapshot.setAccountId(accountId);
        snapshot.setAvailableBalance(balanceOf(accountId));
        return snapshot;
    }

    public synchronized List<PositionSnapshot> positionSnapshots(String accountId) {
        Map<String, BigDecimal> pos = positionMap.getOrDefault(accountId, Map.of());
        List<PositionSnapshot> list = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : pos.entrySet()) {
            PositionSnapshot snapshot = new PositionSnapshot();
            snapshot.setAccountId(accountId);
            snapshot.setSymbol(entry.getKey());
            snapshot.setQuantity(entry.getValue());
            list.add(snapshot);
        }
        return list;
    }

    private void addPosition(String accountId, String symbol, BigDecimal qty) {
        positionMap.computeIfAbsent(accountId, k -> new HashMap<>());
        Map<String, BigDecimal> accountPosition = positionMap.get(accountId);
        accountPosition.put(symbol, accountPosition.getOrDefault(symbol, BigDecimal.ZERO).add(qty));
    }
}
