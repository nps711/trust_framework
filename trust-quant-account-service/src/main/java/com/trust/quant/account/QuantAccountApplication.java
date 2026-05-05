package com.trust.quant.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.trust.quant.account.infrastructure.persistence.mapper")
public class QuantAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuantAccountApplication.class, args);
    }
}
