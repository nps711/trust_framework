package com.trust.quant.trade.domain.service;

import com.trust.quant.trade.domain.model.Trade;
import com.trust.quant.trade.infrastructure.persistence.InMemoryLedger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettlementService {
    private final InMemoryLedger ledger;

    public SettlementService(InMemoryLedger ledger) {
        this.ledger = ledger;
    }

    public void settle(List<Trade> trades) {
        ledger.settle(trades);
    }
}
