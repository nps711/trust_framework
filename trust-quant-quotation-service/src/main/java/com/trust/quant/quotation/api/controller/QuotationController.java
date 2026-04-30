package com.trust.quant.quotation.api.controller;

import com.trust.common.core.api.R;
import com.trust.quant.quotation.api.request.HistoryTickReq;
import com.trust.quant.quotation.api.request.LatestPriceReq;
import com.trust.quant.quotation.api.request.LimitPriceReq;
import com.trust.quant.quotation.api.response.*;
import com.trust.quant.quotation.application.QuotationApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quant/quotation")
public class QuotationController {

    private final QuotationApplicationService applicationService;

    public QuotationController(QuotationApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/latest-price")
    public R<LatestPriceRes> queryLatestPrice(@Valid @RequestBody LatestPriceReq req) {
        return R.success(applicationService.queryLatestPrice(req));
    }

    @PostMapping("/limit-price")
    public R<LimitPriceRes> queryLimitPrice(@Valid @RequestBody LimitPriceReq req) {
        return R.success(applicationService.queryLimitPrice(req));
    }

    @PostMapping("/history-ticks")
    public R<HistoryTickRes> queryHistoryTicks(@Valid @RequestBody HistoryTickReq req) {
        return R.success(applicationService.queryHistoryTicks(req));
    }

    @PostMapping("/symbols")
    public R<SymbolListRes> queryAllSymbols() {
        return R.success(applicationService.queryAllSymbols());
    }
}
