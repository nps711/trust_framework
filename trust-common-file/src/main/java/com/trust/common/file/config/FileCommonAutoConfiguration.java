package com.trust.common.file.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trust.common.file.export.LocalTradeReportExporter;
import com.trust.common.file.export.TradeReportExporter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FileCommonAutoConfiguration {
    @Bean
    public TradeReportExporter tradeReportExporter(ObjectMapper objectMapper) {
        return new LocalTradeReportExporter(objectMapper);
    }
}
