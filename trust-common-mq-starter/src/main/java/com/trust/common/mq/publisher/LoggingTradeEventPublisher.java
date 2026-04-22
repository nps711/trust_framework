package com.trust.common.mq.publisher;

import com.trust.common.mq.event.TradeEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTradeEventPublisher implements TradeEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingTradeEventPublisher.class);

    @Override
    public void publishTradeEvent(TradeEventMessage message) {
        log.info("publish trade event (log fallback): tradeId={} symbol={} qty={} price={}",
                message.getTradeId(), message.getSymbol(), message.getQuantity(), message.getPrice());
    }
}
