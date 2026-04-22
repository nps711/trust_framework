package com.trust.quant.trade.domain.service;

import com.trust.common.config.properties.TrustQuantProperties;
import com.trust.quant.trade.domain.model.RiskDecision;
import com.trust.quant.trade.infrastructure.persistence.mapper.AccountMapper;
import com.trust.quant.trade.infrastructure.persistence.model.AccountEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RiskEvaluator {
    private final TrustQuantProperties properties;
    private final AccountMapper accountMapper;

    public RiskEvaluator(TrustQuantProperties properties, AccountMapper accountMapper) {
        this.properties = properties;
        this.accountMapper = accountMapper;
    }

    public RiskDecision evaluateGeneral(String accountId, BigDecimal price, BigDecimal quantity) {
        BigDecimal notional = price.multiply(quantity);
        if (notional.compareTo(properties.getMaxNotional()) > 0) {
            return new RiskDecision(false, "notional exceeds threshold");
        }

        AccountEntity accountEntity = accountMapper.findByAccountId(accountId);
        BigDecimal available = accountEntity == null ? BigDecimal.ZERO : accountEntity.getAvailableBalance();
        if (available.compareTo(notional) < 0) {
            return new RiskDecision(false, "insufficient balance");
        }

        return new RiskDecision(true, "passed");
    }
}
