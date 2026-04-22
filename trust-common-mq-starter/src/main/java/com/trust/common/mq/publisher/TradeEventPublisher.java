package com.trust.common.mq.publisher;

import com.trust.common.mq.event.TradeEventMessage;

public interface TradeEventPublisher {
    void publishTradeEvent(TradeEventMessage message);
}
