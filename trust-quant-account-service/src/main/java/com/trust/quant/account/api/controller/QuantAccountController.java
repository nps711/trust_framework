package com.trust.quant.account.api.controller;

import com.trust.common.core.api.R;
import com.trust.common.core.context.UserContext;
import com.trust.common.core.context.UserContextHolder;
import com.trust.quant.account.api.request.AccountInfoReq;
import com.trust.quant.account.api.response.AccountInfoRes;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quant/account")
public class QuantAccountController {
    private static final Logger log = LoggerFactory.getLogger(QuantAccountController.class);

    @PostMapping("/info")
    public R<AccountInfoRes> info(@Valid @RequestBody AccountInfoReq req) {
        UserContext context = UserContextHolder.getContext();
        String traceId = context == null ? null : context.getTraceId();
        log.info("account service request received, traceId={}, accountId={}", traceId, req.getAccountId());

        AccountInfoRes res = new AccountInfoRes();
        res.setAccountId(req.getAccountId());
        res.setAvailableBalance("1000000.000000");
        return R.success(res, traceId);
    }
}
