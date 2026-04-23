package com.trust.common.web.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.trust.common.web.filter.TrustContextFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@AutoConfiguration
public class WebCommonAutoConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer precisionCustomizer() {
        return builder -> {
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            builder.serializerByType(BigDecimal.class, ToStringSerializer.instance);
        };
    }

    @Bean
    public FilterRegistrationBean<TrustContextFilter> trustContextFilterRegistration() {
        FilterRegistrationBean<TrustContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TrustContextFilter());
        registrationBean.setOrder(Integer.MIN_VALUE);
        return registrationBean;
    }
}
