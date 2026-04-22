package com.trust.quant.trade.domain;

import com.trust.quant.trade.domain.model.Order;
import com.trust.quant.trade.domain.model.Trade;
import com.trust.quant.trade.domain.type.OrderSide;
import com.trust.quant.trade.domain.type.OrderStatus;
import com.trust.quant.trade.domain.type.OrderType;
import com.trust.quant.trade.domain.type.TimeInForce;
import com.trust.quant.trade.infrastructure.persistence.InMemoryOrderBook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class InMemoryOrderBookTest {

    @Test
    void shouldMatchFull() {
        InMemoryOrderBook book = new InMemoryOrderBook();
        book.add(order(1, "A1", "IF888", OrderSide.BUY, "100", "2", LocalDateTime.now().minusSeconds(2)));
        book.add(order(2, "A2", "IF888", OrderSide.SELL, "99", "2", LocalDateTime.now().minusSeconds(1)));

        List<Trade> trades = book.match("IF888", new AtomicLong(10)::incrementAndGet);
        Assertions.assertEquals(1, trades.size());
        Assertions.assertEquals(0, trades.get(0).getQuantity().compareTo(new BigDecimal("2")));
    }

    @Test
    void shouldMatchPartial() {
        InMemoryOrderBook book = new InMemoryOrderBook();
        book.add(order(1, "A1", "IF888", OrderSide.BUY, "100", "5", LocalDateTime.now().minusSeconds(2)));
        book.add(order(2, "A2", "IF888", OrderSide.SELL, "100", "2", LocalDateTime.now().minusSeconds(1)));

        List<Trade> trades = book.match("IF888", new AtomicLong(10)::incrementAndGet);
        Assertions.assertEquals(1, trades.size());
        Order buy = book.get(1).orElseThrow();
        Assertions.assertEquals(OrderStatus.PARTIALLY_FILLED, buy.getStatus());
        Assertions.assertEquals(0, buy.getRemainingQuantity().compareTo(new BigDecimal("3")));
    }

    @Test
    void shouldNotMatchWhenPriceCrossFails() {
        InMemoryOrderBook book = new InMemoryOrderBook();
        book.add(order(1, "A1", "IF888", OrderSide.BUY, "98", "2", LocalDateTime.now().minusSeconds(2)));
        book.add(order(2, "A2", "IF888", OrderSide.SELL, "99", "2", LocalDateTime.now().minusSeconds(1)));

        List<Trade> trades = book.match("IF888", new AtomicLong(10)::incrementAndGet);
        Assertions.assertTrue(trades.isEmpty());
    }

    @Test
    void shouldNotMatchCanceledOrder() {
        InMemoryOrderBook book = new InMemoryOrderBook();
        book.add(order(1, "A1", "IF888", OrderSide.BUY, "100", "2", LocalDateTime.now().minusSeconds(3)));
        book.add(order(2, "A2", "IF888", OrderSide.SELL, "100", "2", LocalDateTime.now().minusSeconds(2)));
        boolean canceled = book.cancel(2, "A2");
        Assertions.assertTrue(canceled);

        List<Trade> trades = book.match("IF888", new AtomicLong(10)::incrementAndGet);
        Assertions.assertTrue(trades.isEmpty());
    }

    @Test
    void shouldMatchByTimePriorityAtSamePrice() {
        InMemoryOrderBook book = new InMemoryOrderBook();
        book.add(order(1, "Buyer", "IF888", OrderSide.BUY, "100", "1", LocalDateTime.now().minusSeconds(5)));
        book.add(order(2, "SellerOld", "IF888", OrderSide.SELL, "100", "1", LocalDateTime.now().minusSeconds(4)));
        book.add(order(3, "SellerNew", "IF888", OrderSide.SELL, "100", "1", LocalDateTime.now().minusSeconds(2)));

        List<Trade> trades = book.match("IF888", new AtomicLong(10)::incrementAndGet);
        Assertions.assertEquals(1, trades.size());
        Assertions.assertEquals("SellerOld", trades.get(0).getSellAccountId());
    }

    private Order order(long id, String account, String symbol, OrderSide side, String price, String qty, LocalDateTime time) {
        Order order = new Order();
        order.setOrderId(id);
        order.setAccountId(account);
        order.setSymbol(symbol);
        order.setSide(side);
        order.setOrderType(OrderType.LIMIT);
        order.setTimeInForce(TimeInForce.GTC);
        order.setPrice(new BigDecimal(price));
        order.setQuantity(new BigDecimal(qty));
        order.setRemainingQuantity(new BigDecimal(qty));
        order.setStatus(OrderStatus.NEW);
        order.setCreateTime(time);
        order.setUpdateTime(time);
        return order;
    }
}
