package com.trust.quant.quotation.domain.service;

import com.trust.quant.quotation.api.response.TickSnapshot;
import com.trust.quant.quotation.domain.model.SymbolInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MarketDataSimulator {

    private final Map<String, SymbolInfo> symbolMap = new ConcurrentHashMap<>();
    private final Map<String, List<TickSnapshot>> historyMap = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        String[][] symbols = {
                {"000001.SZ", "平安银行"},
                {"000002.SZ", "万科A"},
                {"600000.SH", "浦发银行"},
                {"600036.SH", "招商银行"},
                {"000858.SZ", "五粮液"},
                {"600519.SH", "贵州茅台"},
                {"000333.SZ", "美的集团"},
                {"600276.SH", "恒瑞医药"},
                {"002415.SZ", "海康威视"},
                {"601318.SH", "中国平安"},
                {"000725.SZ", "京东方A"},
                {"600030.SH", "中信证券"},
                {"002594.SZ", "比亚迪"},
                {"601012.SH", "隆基绿能"},
                {"300750.SZ", "宁德时代"}
        };

        for (String[] s : symbols) {
            SymbolInfo info = new SymbolInfo();
            info.setSymbol(s[0]);
            info.setName(s[1]);
            BigDecimal basePrice = BigDecimal.valueOf(10 + random.nextDouble() * 190);
            basePrice = basePrice.setScale(2, RoundingMode.HALF_UP);
            info.setPreClosePrice(basePrice);
            info.setOpenPrice(basePrice.multiply(BigDecimal.valueOf(1 + (random.nextDouble() - 0.5) * 0.02)).setScale(2, RoundingMode.HALF_UP));
            info.setLatestPrice(info.getOpenPrice());
            info.setHighPrice(info.getOpenPrice().max(basePrice));
            info.setLowPrice(info.getOpenPrice().min(basePrice));
            info.setUpdateTime(LocalDateTime.now());
            symbolMap.put(s[0], info);
            historyMap.put(s[0], new CopyOnWriteArrayList<>());
        }
    }

    @Scheduled(fixedRate = 1000)
    public void simulatePriceChange() {
        for (SymbolInfo info : symbolMap.values()) {
            double change = (random.nextDouble() - 0.5) * 0.01;
            BigDecimal newPrice = info.getLatestPrice().multiply(BigDecimal.valueOf(1 + change))
                    .setScale(2, RoundingMode.HALF_UP);

            info.setLatestPrice(newPrice);
            if (newPrice.compareTo(info.getHighPrice()) > 0) {
                info.setHighPrice(newPrice);
            }
            if (newPrice.compareTo(info.getLowPrice()) < 0) {
                info.setLowPrice(newPrice);
            }
            info.setUpdateTime(LocalDateTime.now());

            TickSnapshot tick = toTickSnapshot(info);
            List<TickSnapshot> history = historyMap.computeIfAbsent(info.getSymbol(), k -> new CopyOnWriteArrayList<>());
            history.add(tick);
            if (history.size() > 1000) {
                history.remove(0);
            }
        }
    }

    public TickSnapshot getLatestTick(String symbol) {
        SymbolInfo info = symbolMap.get(symbol);
        if (info == null) {
            return null;
        }
        return toTickSnapshot(info);
    }

    public List<TickSnapshot> getHistoryTicks(String symbol, int limit) {
        List<TickSnapshot> history = historyMap.getOrDefault(symbol, new ArrayList<>());
        int size = history.size();
        if (size <= limit) {
            return new ArrayList<>(history);
        }
        return new ArrayList<>(history.subList(size - limit, size));
    }

    public List<String> getAllSymbols() {
        return new ArrayList<>(symbolMap.keySet());
    }

    private TickSnapshot toTickSnapshot(SymbolInfo info) {
        TickSnapshot tick = new TickSnapshot();
        tick.setSymbol(info.getSymbol());
        tick.setLatestPrice(info.getLatestPrice());
        tick.setOpenPrice(info.getOpenPrice());
        tick.setHighPrice(info.getHighPrice());
        tick.setLowPrice(info.getLowPrice());
        tick.setPreClosePrice(info.getPreClosePrice());
        BigDecimal upLimit = info.getPreClosePrice().multiply(BigDecimal.valueOf(1.10)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal downLimit = info.getPreClosePrice().multiply(BigDecimal.valueOf(0.90)).setScale(2, RoundingMode.HALF_UP);
        tick.setUpLimitPrice(upLimit);
        tick.setDownLimitPrice(downLimit);
        tick.setUpdateTime(info.getUpdateTime());
        return tick;
    }
}
