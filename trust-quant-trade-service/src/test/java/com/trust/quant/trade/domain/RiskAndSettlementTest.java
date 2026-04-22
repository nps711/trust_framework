package com.trust.quant.trade.domain;

import com.trust.common.config.properties.TrustQuantProperties;
import com.trust.quant.trade.domain.model.RiskDecision;
import com.trust.quant.trade.domain.service.RiskEvaluator;
import com.trust.quant.trade.infrastructure.persistence.mapper.AccountMapper;
import com.trust.quant.trade.infrastructure.persistence.model.AccountEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

class RiskAndSettlementTest {

    @Test
    void shouldRejectWhenExceedsThresholdAndAllowWhenThresholdUpdated() {
        TrustQuantProperties properties = new TrustQuantProperties();
        properties.setMaxNotional(new BigDecimal("100"));

        AccountMapper accountMapper = Mockito.mock(AccountMapper.class);
        AccountEntity entity = new AccountEntity();
        entity.setAccountId("A1");
        entity.setAvailableBalance(new BigDecimal("1000000"));
        Mockito.when(accountMapper.findByAccountId("A1")).thenReturn(entity);

        RiskEvaluator evaluator = new RiskEvaluator(properties, accountMapper);

        RiskDecision reject = evaluator.evaluateGeneral("A1", new BigDecimal("20"), new BigDecimal("10"));
        Assertions.assertFalse(reject.isPassed());

        properties.setMaxNotional(new BigDecimal("1000"));
        RiskDecision pass = evaluator.evaluateGeneral("A1", new BigDecimal("20"), new BigDecimal("10"));
        Assertions.assertTrue(pass.isPassed());
    }

    @Test
    void shouldRejectWhenBalanceInsufficient() {
        TrustQuantProperties properties = new TrustQuantProperties();
        properties.setMaxNotional(new BigDecimal("1000"));

        AccountMapper accountMapper = Mockito.mock(AccountMapper.class);
        AccountEntity entity = new AccountEntity();
        entity.setAccountId("A1");
        entity.setAvailableBalance(new BigDecimal("10"));
        Mockito.when(accountMapper.findByAccountId("A1")).thenReturn(entity);

        RiskEvaluator evaluator = new RiskEvaluator(properties, accountMapper);
        RiskDecision reject = evaluator.evaluateGeneral("A1", new BigDecimal("20"), new BigDecimal("1"));

        Assertions.assertFalse(reject.isPassed());
    }
}
