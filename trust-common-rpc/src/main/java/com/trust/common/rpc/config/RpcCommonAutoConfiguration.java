package com.trust.common.rpc.config;

import com.trust.common.rpc.interceptor.ContextFeignInterceptor;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class RpcCommonAutoConfiguration {
    @Bean
    public RequestInterceptor contextRequestInterceptor() {
        return new ContextFeignInterceptor();
    }
}
