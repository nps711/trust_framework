package com.trust.common.mq.config;

import com.trust.common.mq.publisher.LoggingTradeEventPublisher;
import com.trust.common.mq.publisher.RabbitTradeEventPublisher;
import com.trust.common.mq.publisher.TradeEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class MqCommonAutoConfiguration {

    @Bean
    @ConditionalOnBean(RabbitTemplate.class)
    @ConditionalOnProperty(prefix = "trust.mq", name = "rabbit-enabled", havingValue = "true")
    public TradeEventPublisher rabbitTradeEventPublisher(RabbitTemplate rabbitTemplate) {
        return new RabbitTradeEventPublisher(rabbitTemplate, "quant.trade.exchange", "quant.trade.executed");
    }

    @Bean
    @ConditionalOnMissingBean(TradeEventPublisher.class)
    public TradeEventPublisher loggingTradeEventPublisher() {
        return new LoggingTradeEventPublisher();
    }
}
