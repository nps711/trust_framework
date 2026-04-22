package com.trust.quant.trade.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI quantTradeOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Trust Quant Trade Service API")
                .description("Quant trade lightweight service APIs")
                .version("v1"));
    }

//    @Bean
//    public WebSecurityCustomizer swaggerWebSecurityCustomizer() {
//        return web -> web.ignoring()
//                .requestMatchers(
//                        "/swagger-ui.html",
//                        "/swagger-ui/**",
//                        "/v3/api-docs/**");
//    }
}
