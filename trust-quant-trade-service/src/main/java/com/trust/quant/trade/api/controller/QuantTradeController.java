package com.trust.quant.trade.api.controller;

import com.trust.common.core.api.R;
import com.trust.common.core.context.UserContextHolder;
import com.trust.quant.trade.api.request.AccountQueryReq;
import com.trust.quant.trade.api.request.OrderCancelReq;
import com.trust.quant.trade.api.request.OrderPlaceReq;
import com.trust.quant.trade.api.request.OrderQueryReq;
import com.trust.quant.trade.api.request.TradeQueryReq;
import com.trust.quant.trade.api.response.AccountSnapshot;
import com.trust.quant.trade.api.response.OrderPlaceRes;
import com.trust.quant.trade.application.QuantTradeApplicationService;
import com.trust.quant.trade.domain.model.Order;
import com.trust.quant.trade.domain.model.Trade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quant")
@Tag(name = "QuantTrade", description = "Quant trade core APIs")
public class QuantTradeController {
    private final QuantTradeApplicationService applicationService;

    public QuantTradeController(QuantTradeApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/order/place")
    @Operation(summary = "Place order")
    public R<OrderPlaceRes> place(@Valid @RequestBody OrderPlaceReq req) {
        return R.success(applicationService.placeOrder(req), traceId());
    }

    @PostMapping("/order/cancel")
    @Operation(summary = "Cancel order")
    public R<Boolean> cancel(@Valid @RequestBody OrderCancelReq req) {
        return R.success(applicationService.cancelOrder(req), traceId());
    }

//    @DataScope(userAlias = "accountId")
    @PostMapping("/order/query")
    @Operation(summary = "Query orders")
    public R<List<Order>> queryOrder(@Valid @RequestBody OrderQueryReq req) {
        return R.success(applicationService.queryOrders(req), traceId());
    }

    @PostMapping("/trade/query")
    @Operation(summary = "Query trades")
    public R<List<Trade>> queryTrade(@Valid @RequestBody TradeQueryReq req) {
        return R.success(applicationService.queryTrades(req), traceId());
    }

    @PostMapping("/account/query")
    @Operation(summary = "Query account")
    public R<AccountSnapshot> queryAccount(@Valid @RequestBody AccountQueryReq req) {
        return R.success(applicationService.queryAccount(req), traceId());
    }

    @PostMapping("/account/query-remote")
    @Operation(summary = "Query account from quant-account-service")
    public R<AccountSnapshot> queryAccountRemote(@Valid @RequestBody AccountQueryReq req) {
        String traceId = traceId();
        return R.success(applicationService.queryRemoteAccount(req, traceId), traceId);
    }

    private String traceId() {
        return UserContextHolder.getContext() == null ? null : UserContextHolder.getContext().getTraceId();
    }
}
