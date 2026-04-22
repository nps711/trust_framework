package com.trust.quant.trade.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuantTradeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRejectWhenNoToken() throws Exception {
        mockMvc.perform(post("/quant/order/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountId", "A1"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void shouldRunPlaceMatchAndQueryWithDbIdempotency() throws Exception {
        String auth = "Bearer demo-user";

        mockMvc.perform(post("/quant/order/place")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .header("x-user-id", "buyer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requestId", "REQ-BUY-1",
                                "accountId", "BUYER",
                                "symbol", "IF888",
                                "side", "BUY",
                                "orderType", "LIMIT",
                                "timeInForce", "GTC",
                                "price", "101",
                                "quantity", "1"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/quant/order/place")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .header("x-user-id", "seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requestId", "REQ-SELL-1",
                                "accountId", "SELLER",
                                "symbol", "IF888",
                                "side", "SELL",
                                "orderType", "LIMIT",
                                "timeInForce", "GTC",
                                "price", "100",
                                "quantity", "1"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/quant/order/query")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountId", "BUYER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].symbol").value("IF888"));

        mockMvc.perform(post("/quant/trade/query")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountId", "BUYER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].symbol").value("IF888"));

        mockMvc.perform(post("/quant/account/query")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountId", "BUYER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accountId").value("BUYER"));

        mockMvc.perform(post("/quant/order/place")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .header("x-user-id", "buyer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "requestId", "REQ-BUY-1",
                                "accountId", "BUYER",
                                "symbol", "IF888",
                                "side", "BUY",
                                "orderType", "LIMIT",
                                "timeInForce", "GTC",
                                "price", "101",
                                "quantity", "1"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(10001));
    }

    @Test
    void removedEndpointsShouldReturnNotFound() throws Exception {
        String auth = "Bearer demo-user";
        mockMvc.perform(post("/quant/report/export")
                        .header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("accountId", "BUYER"))))
                .andExpect(status().isNotFound());
    }
}
