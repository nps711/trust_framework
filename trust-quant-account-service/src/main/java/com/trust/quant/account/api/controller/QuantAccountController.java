package com.trust.quant.account.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trust.common.core.api.R;
import com.trust.common.core.context.UserContext;
import com.trust.common.core.context.UserContextHolder;
import com.trust.common.core.error.BusinessException;
import com.trust.quant.account.api.request.AccountInfoReq;
import com.trust.quant.account.api.response.AccountInfoRes;
import com.trust.quant.account.infrastructure.persistence.entity.QuantAccount;
import com.trust.quant.account.infrastructure.persistence.mapper.QuantAccountMapper;
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

    private final QuantAccountMapper quantAccountMapper;

    public QuantAccountController(QuantAccountMapper quantAccountMapper) {
        this.quantAccountMapper = quantAccountMapper;
    }

    @PostMapping("/info")
    public R<AccountInfoRes> info(@Valid @RequestBody AccountInfoReq req) {
        UserContext context = UserContextHolder.getContext();
        String traceId = context == null ? null : context.getTraceId();
        log.info("account service request received, traceId={}, accountId={}", traceId, req.getAccountId());

        QuantAccount account = quantAccountMapper.selectOne(
                new LambdaQueryWrapper<QuantAccount>()
                        .eq(QuantAccount::getAccountId, req.getAccountId()));
        if (account == null) {
            throw new BusinessException("account not found: " + req.getAccountId());
        }

        AccountInfoRes res = new AccountInfoRes();
        res.setAccountId(account.getAccountId());
        res.setAvailableBalance(account.getAvailableBalance().toPlainString());
        return R.success(res);
    }
}
