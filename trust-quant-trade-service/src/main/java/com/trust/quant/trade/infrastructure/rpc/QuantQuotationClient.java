package com.trust.quant.trade.infrastructure.rpc;

import com.trust.common.core.api.R;
import com.trust.quant.trade.infrastructure.rpc.dto.LatestPriceReq;
import com.trust.quant.trade.infrastructure.rpc.dto.LatestPriceRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trust-quant-quotation-service", url = "${trust.quant.quotation-service.url:http://localhost:8088}")
public interface QuantQuotationClient {

    @PostMapping("/quant/quotation/latest-price")
    R<LatestPriceRes> queryLatestPrice(@RequestBody LatestPriceReq req);
}
