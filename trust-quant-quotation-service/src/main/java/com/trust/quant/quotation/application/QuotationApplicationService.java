package com.trust.quant.quotation.application;

import com.trust.quant.quotation.api.request.HistoryTickReq;
import com.trust.quant.quotation.api.request.LatestPriceReq;
import com.trust.quant.quotation.api.request.LimitPriceReq;
import com.trust.quant.quotation.api.response.*;
import com.trust.quant.quotation.domain.service.MarketDataSimulator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuotationApplicationService {

    private final MarketDataSimulator marketDataSimulator;

    public QuotationApplicationService(MarketDataSimulator marketDataSimulator) {
        this.marketDataSimulator = marketDataSimulator;
    }

    public LatestPriceRes queryLatestPrice(LatestPriceReq req) {
        LatestPriceRes res = new LatestPriceRes();
        res.setTick(marketDataSimulator.getLatestTick(req.getSymbol()));
        return res;
    }

    public LimitPriceRes queryLimitPrice(LimitPriceReq req) {
        LimitPriceRes res = new LimitPriceRes();
        res.setTick(marketDataSimulator.getLatestTick(req.getSymbol()));
        return res;
    }

    public HistoryTickRes queryHistoryTicks(HistoryTickReq req) {
        HistoryTickRes res = new HistoryTickRes();
        res.setTicks(marketDataSimulator.getHistoryTicks(req.getSymbol(), req.getLimit()));
        return res;
    }

    public SymbolListRes queryAllSymbols() {
        SymbolListRes res = new SymbolListRes();
        res.setSymbols(marketDataSimulator.getAllSymbols());
        return res;
    }
}
