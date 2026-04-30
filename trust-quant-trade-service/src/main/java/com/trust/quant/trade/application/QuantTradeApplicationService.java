package com.trust.quant.trade.application;

import com.trust.common.core.api.R;
import com.trust.common.core.error.BusinessException;
import com.trust.common.core.id.SnowflakeIdGenerator;
import com.trust.common.log.annotation.AuditLog;
import com.trust.quant.trade.api.request.AccountQueryReq;
import com.trust.quant.trade.api.request.OrderCancelReq;
import com.trust.quant.trade.api.request.OrderPlaceReq;
import com.trust.quant.trade.api.request.OrderQueryReq;
import com.trust.quant.trade.api.request.TradeQueryReq;
import com.trust.quant.trade.api.response.AccountSnapshot;
import com.trust.quant.trade.api.response.OrderPlaceRes;
import com.trust.quant.trade.domain.model.Order;
import com.trust.quant.trade.domain.model.RiskDecision;
import com.trust.quant.trade.domain.model.Trade;
import com.trust.quant.trade.domain.service.RiskEvaluator;
import com.trust.quant.trade.domain.type.OrderStatus;
import com.trust.quant.trade.infrastructure.persistence.InMemoryOrderBook;
import com.trust.quant.trade.infrastructure.persistence.mapper.AccountMapper;
import com.trust.quant.trade.infrastructure.persistence.mapper.IdempotencyLogMapper;
import com.trust.quant.trade.infrastructure.persistence.mapper.OrderMapper;
import com.trust.quant.trade.infrastructure.persistence.mapper.TradeMapper;
import com.trust.quant.trade.infrastructure.persistence.model.AccountEntity;
import com.trust.quant.trade.infrastructure.rpc.QuantAccountClient;
import com.trust.quant.trade.infrastructure.rpc.QuantQuotationClient;
import com.trust.quant.trade.infrastructure.rpc.dto.AccountInfoReq;
import com.trust.quant.trade.infrastructure.rpc.dto.AccountInfoRes;
import com.trust.quant.trade.infrastructure.rpc.dto.LatestPriceReq;
import com.trust.quant.trade.infrastructure.rpc.dto.LatestPriceRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class QuantTradeApplicationService {
    private static final String IDEMPOTENCY_BIZ_PLACE_ORDER = "PLACE_ORDER";
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("1000000.000000");

    private final InMemoryOrderBook orderBook;
    private final RiskEvaluator riskEvaluator;
    private final SnowflakeIdGenerator idGenerator;
    private final OrderMapper orderMapper;
    private final TradeMapper tradeMapper;
    private final AccountMapper accountMapper;
    private final IdempotencyLogMapper idempotencyLogMapper;
    private final QuantAccountClient quantAccountClient;
    private final QuantQuotationClient quantQuotationClient;

    public QuantTradeApplicationService(InMemoryOrderBook orderBook,
                                        RiskEvaluator riskEvaluator,
                                        SnowflakeIdGenerator idGenerator,
                                        OrderMapper orderMapper,
                                        TradeMapper tradeMapper,
                                        AccountMapper accountMapper,
                                        IdempotencyLogMapper idempotencyLogMapper,
                                        QuantAccountClient quantAccountClient,
                                        QuantQuotationClient quantQuotationClient) {
        this.orderBook = orderBook;
        this.riskEvaluator = riskEvaluator;
        this.idGenerator = idGenerator;
        this.orderMapper = orderMapper;
        this.tradeMapper = tradeMapper;
        this.accountMapper = accountMapper;
        this.idempotencyLogMapper = idempotencyLogMapper;
        this.quantAccountClient = quantAccountClient;
        this.quantQuotationClient = quantQuotationClient;
    }

    @AuditLog(module = "quant-trade", action = "place-order")
    public OrderPlaceRes placeOrder(OrderPlaceReq req) {
        if (req.getRequestId() == null || req.getRequestId().isBlank()) {
            throw new BusinessException("requestId is required for idempotency");
        }

        // 查询行情价格
        queryAndValidateMarketPrice(req.getSymbol(), req.getPrice());

        recordIdempotency(req.getRequestId());
        ensureAccountExists(req.getAccountId());

        RiskDecision riskDecision = riskEvaluator.evaluateGeneral(req.getAccountId(), req.getPrice(), req.getQuantity());
        if (!riskDecision.isPassed()) {
            throw new BusinessException("risk rejected: " + riskDecision.getReason());
        }

        Order order = buildOrder(req);
        orderBook.add(order);
        orderMapper.insert(order);

        List<Trade> trades = orderBook.match(req.getSymbol(), idGenerator::nextId);
        if (!trades.isEmpty()) {
            persistTradesAndSettle(trades);
            syncMatchedOrderState(trades);
        }

        OrderPlaceRes res = new OrderPlaceRes();
        res.setOrderId(order.getOrderId());
        res.setStatus(order.getStatus());
        res.setMessage("accepted");
        return res;
    }

    @AuditLog(module = "quant-trade", action = "cancel-order")
    public boolean cancelOrder(OrderCancelReq req) {
        Order order = orderMapper.findById(req.getOrderId());
        if (order == null || !order.getAccountId().equals(req.getAccountId())) {
            return false;
        }
        if (order.getStatus() == OrderStatus.FILLED || order.getStatus() == OrderStatus.CANCELED) {
            return false;
        }
        orderBook.cancel(req.getOrderId(), req.getAccountId());
        return orderMapper.cancelByIdAndAccount(req.getOrderId(), req.getAccountId(), LocalDateTime.now()) > 0;
    }

    public List<Order> queryOrders(OrderQueryReq req) {
        return orderMapper.findByAccountId(req.getAccountId());
    }

    public List<Trade> queryTrades(TradeQueryReq req) {
        return tradeMapper.findByAccountId(req.getAccountId());
    }

    public AccountSnapshot queryAccount(AccountQueryReq req) {
        ensureAccountExists(req.getAccountId());
        AccountEntity accountEntity = accountMapper.findByAccountId(req.getAccountId());
        AccountSnapshot snapshot = new AccountSnapshot();
        snapshot.setAccountId(req.getAccountId());
        snapshot.setAvailableBalance(accountEntity.getAvailableBalance());
        return snapshot;
    }

    public AccountSnapshot queryRemoteAccount(AccountQueryReq req, String tradeTraceId) {
        AccountInfoReq remoteReq = new AccountInfoReq();
        remoteReq.setAccountId(req.getAccountId());
        AccountInfoRes remoteRes = quantAccountClient.queryAccount(remoteReq).getData();
        log.info("remote account query completed, tradeTraceId={}, accountId={}", tradeTraceId, req.getAccountId());

        AccountSnapshot snapshot = new AccountSnapshot();
        snapshot.setAccountId(remoteRes == null ? null : remoteRes.getAccountId());
        if (remoteRes != null && remoteRes.getAvailableBalance() != null) {
            snapshot.setAvailableBalance(new BigDecimal(remoteRes.getAvailableBalance()));
        }
        return snapshot;
    }

    private void recordIdempotency(String requestId) {
        try {
            idempotencyLogMapper.insert(idGenerator.nextId(), requestId, IDEMPOTENCY_BIZ_PLACE_ORDER, LocalDateTime.now());
        } catch (DuplicateKeyException ex) {
            throw new BusinessException("duplicate request");
        }
    }

    private void ensureAccountExists(String accountId) {
        if (accountMapper.findByAccountId(accountId) != null) {
            return;
        }
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(idGenerator.nextId());
        accountEntity.setAccountId(accountId);
        accountEntity.setAvailableBalance(DEFAULT_BALANCE);
        accountEntity.setUpdateTime(LocalDateTime.now());
        try {
            accountMapper.insert(accountEntity);
        } catch (DuplicateKeyException ignored) {
            // Another request created the account first.
        }
    }

    private void persistTradesAndSettle(List<Trade> trades) {
        for (Trade trade : trades) {
            tradeMapper.insert(trade);
            settleTrade(trade);
        }
    }

    private void settleTrade(Trade trade) {
        ensureAccountExists(trade.getBuyAccountId());
        ensureAccountExists(trade.getSellAccountId());

        BigDecimal notional = trade.getPrice().multiply(trade.getQuantity()).setScale(6, RoundingMode.HALF_UP);
        applyBalanceChange(trade.getBuyAccountId(), notional.negate());
        applyBalanceChange(trade.getSellAccountId(), notional);
    }

    private void applyBalanceChange(String accountId, BigDecimal delta) {
        AccountEntity account = accountMapper.findByAccountId(accountId);
        BigDecimal newBalance = account.getAvailableBalance().add(delta).setScale(6, RoundingMode.HALF_UP);
        accountMapper.updateBalance(accountId, newBalance, LocalDateTime.now());
    }

    private void syncMatchedOrderState(List<Trade> trades) {
        for (Trade trade : trades) {
            orderBook.get(trade.getBuyOrderId()).ifPresent(orderMapper::updateState);
            orderBook.get(trade.getSellOrderId()).ifPresent(orderMapper::updateState);
        }
    }

    private void queryAndValidateMarketPrice(String symbol, BigDecimal orderPrice) {
        try {
            LatestPriceReq priceReq = new LatestPriceReq();
            priceReq.setSymbol(symbol);
            R<LatestPriceRes> priceRes = quantQuotationClient.queryLatestPrice(priceReq);
            if (priceRes != null && priceRes.getData() != null && priceRes.getData().getTick() != null) {
                LatestPriceRes.TickSnapshot tick = priceRes.getData().getTick();
                log.info("market price query success, symbol={}, latestPrice={}, upLimit={}, downLimit={}",
                        symbol, tick.getLatestPrice(), tick.getUpLimitPrice(), tick.getDownLimitPrice());
                if (orderPrice != null && tick.getUpLimitPrice() != null && orderPrice.compareTo(tick.getUpLimitPrice()) > 0) {
                    throw new BusinessException("order price exceeds up limit: " + tick.getUpLimitPrice());
                }
                if (orderPrice != null && tick.getDownLimitPrice() != null && orderPrice.compareTo(tick.getDownLimitPrice()) < 0) {
                    throw new BusinessException("order price below down limit: " + tick.getDownLimitPrice());
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("market price query failed, symbol={}, error={}", symbol, e.getMessage());
        }
    }

    private Order buildOrder(OrderPlaceReq req) {
        Order order = new Order();
        order.setOrderId(idGenerator.nextId());
        order.setRequestId(req.getRequestId());
        order.setAccountId(req.getAccountId());
        order.setSymbol(req.getSymbol());
        order.setSide(req.getSide());
        order.setOrderType(req.getOrderType());
        order.setTimeInForce(req.getTimeInForce());
        order.setPrice(req.getPrice().setScale(6, RoundingMode.HALF_UP));
        order.setQuantity(req.getQuantity().setScale(6, RoundingMode.HALF_UP));
        order.setRemainingQuantity(req.getQuantity().setScale(6, RoundingMode.HALF_UP));
        order.setStatus(OrderStatus.NEW);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return order;
    }
}
