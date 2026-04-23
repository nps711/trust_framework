package com.trust.common.mq.publisher;

import com.trust.common.mq.event.TradeEventMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitTradeEventPublisher implements TradeEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitTradeEventPublisher(RabbitTemplate rabbitTemplate, String exchange, String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publishTradeEvent(TradeEventMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
