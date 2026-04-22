package com.trust.quant.trade.infrastructure.rpc;

import com.trust.common.core.api.R;
import com.trust.quant.trade.infrastructure.rpc.dto.AccountInfoReq;
import com.trust.quant.trade.infrastructure.rpc.dto.AccountInfoRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "quantAccountClient", url = "${trust.quant.account-service.url:http://localhost:8091}")
public interface QuantAccountClient {

    @PostMapping("/quant/account/info")
    R<AccountInfoRes> queryAccount(@RequestBody AccountInfoReq req);
}
