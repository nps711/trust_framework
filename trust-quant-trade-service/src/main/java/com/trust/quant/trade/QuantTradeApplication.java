package com.trust.quant.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class QuantTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuantTradeApplication.class, args);
    }
}
