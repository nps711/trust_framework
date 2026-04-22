package com.trust.quant.trade.infrastructure.persistence;

import com.trust.quant.trade.domain.model.Order;
import com.trust.quant.trade.domain.model.Trade;
import com.trust.quant.trade.domain.type.OrderSide;
import com.trust.quant.trade.domain.type.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

@Component
public class InMemoryOrderBook {
    private final Map<String, PriorityQueue<Order>> buyBook = new HashMap<>();
    private final Map<String, PriorityQueue<Order>> sellBook = new HashMap<>();
    private final Map<Long, Order> orderIndex = new HashMap<>();

    public synchronized void add(Order order) {
        orderIndex.put(order.getOrderId(), order);
        queue(order.getSymbol(), order.getSide()).add(order);
    }

    public synchronized Optional<Order> get(long orderId) {
        return Optional.ofNullable(orderIndex.get(orderId));
    }

    public synchronized boolean cancel(long orderId, String accountId) {
        Order order = orderIndex.get(orderId);
        if (order == null || !order.getAccountId().equals(accountId)) {
            return false;
        }
        if (order.getStatus() == OrderStatus.FILLED || order.getStatus() == OrderStatus.CANCELED) {
            return false;
        }
        order.setStatus(OrderStatus.CANCELED);
        order.setUpdateTime(LocalDateTime.now());
        queue(order.getSymbol(), order.getSide()).remove(order);
        return true;
    }

    public synchronized List<Order> listByAccount(String accountId) {
        return orderIndex.values().stream()
                .filter(o -> o.getAccountId().equals(accountId))
                .sorted(Comparator.comparing(Order::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

    public synchronized List<Trade> match(String symbol, LongSupplier tradeIdSupplier) {
        PriorityQueue<Order> buys = buyBook.get(symbol);
        PriorityQueue<Order> sells = sellBook.get(symbol);
        if (buys == null || sells == null) {
            return List.of();
        }
        List<Trade> trades = new ArrayList<>();
        while (!buys.isEmpty() && !sells.isEmpty()) {
            Order buy = buys.peek();
            Order sell = sells.peek();
            if (buy.getPrice().compareTo(sell.getPrice()) < 0) {
                break;
            }
            BigDecimal qty = buy.getRemainingQuantity().min(sell.getRemainingQuantity());
            Trade trade = new Trade();
            trade.setTradeId(tradeIdSupplier.getAsLong());
            trade.setBuyOrderId(buy.getOrderId());
            trade.setSellOrderId(sell.getOrderId());
            trade.setBuyAccountId(buy.getAccountId());
            trade.setSellAccountId(sell.getAccountId());
            trade.setSymbol(symbol);
            trade.setPrice(sell.getPrice());
            trade.setQuantity(qty);
            trade.setTradeTime(LocalDateTime.now());
            trades.add(trade);

            reduceOrder(buy, qty);
            reduceOrder(sell, qty);

            if (buy.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                buy.setStatus(OrderStatus.FILLED);
                buys.poll();
            } else {
                buy.setStatus(OrderStatus.PARTIALLY_FILLED);
            }

            if (sell.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                sell.setStatus(OrderStatus.FILLED);
                sells.poll();
            } else {
                sell.setStatus(OrderStatus.PARTIALLY_FILLED);
            }
            buy.setUpdateTime(LocalDateTime.now());
            sell.setUpdateTime(LocalDateTime.now());
        }
        return trades;
    }

    private PriorityQueue<Order> queue(String symbol, OrderSide side) {
        if (side == OrderSide.BUY) {
            return buyBook.computeIfAbsent(symbol, k -> new PriorityQueue<>(Comparator
                    .comparing(Order::getPrice).reversed()
                    .thenComparing(Order::getCreateTime)));
        }
        return sellBook.computeIfAbsent(symbol, k -> new PriorityQueue<>(Comparator
                .comparing(Order::getPrice)
                .thenComparing(Order::getCreateTime)));
    }

    private void reduceOrder(Order order, BigDecimal matchedQty) {
        order.setRemainingQuantity(order.getRemainingQuantity().subtract(matchedQty));
    }
}
