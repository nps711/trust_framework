package com.trust.common.mq.config;

import com.trust.common.mq.aspect.RabbitListenerAspect;
import com.trust.common.mq.interceptor.ContextMessagePostProcessor;
import com.trust.common.mq.publisher.LoggingTradeEventPublisher;
import com.trust.common.mq.publisher.RabbitTradeEventPublisher;
import com.trust.common.mq.publisher.TradeEventPublisher;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

    @Bean
    @ConditionalOnClass(RabbitTemplate.class)
    public ContextMessagePostProcessor contextMessagePostProcessor() {
        return new ContextMessagePostProcessor();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.amqp.rabbit.annotation.RabbitListener")
    public RabbitListenerAspect rabbitListenerAspect() {
        return new RabbitListenerAspect();
    }

    @AutoConfiguration
    @ConditionalOnBean(RabbitTemplate.class)
    public static class RabbitTemplatePostProcessorConfiguration {

        private final RabbitTemplate rabbitTemplate;
        private final ContextMessagePostProcessor contextMessagePostProcessor;

        public RabbitTemplatePostProcessorConfiguration(RabbitTemplate rabbitTemplate,
                                                        ContextMessagePostProcessor contextMessagePostProcessor) {
            this.rabbitTemplate = rabbitTemplate;
            this.contextMessagePostProcessor = contextMessagePostProcessor;
        }

        @PostConstruct
        public void init() {
            rabbitTemplate.addBeforePublishPostProcessors(contextMessagePostProcessor);
        }
    }
}
