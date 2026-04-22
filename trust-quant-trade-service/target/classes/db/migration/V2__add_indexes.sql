CREATE UNIQUE INDEX IF NOT EXISTS uq_qt_idempotency_req_biz
    ON qt_idempotency_log (request_id, biz_type);

CREATE UNIQUE INDEX IF NOT EXISTS uq_qt_account_account_id
    ON qt_account (account_id);

CREATE INDEX IF NOT EXISTS idx_qt_order_account_create_time
    ON qt_order (account_id, create_time);

CREATE INDEX IF NOT EXISTS idx_qt_trade_buy_account_trade_time
    ON qt_trade (buy_account_id, trade_time);

CREATE INDEX IF NOT EXISTS idx_qt_trade_sell_account_trade_time
    ON qt_trade (sell_account_id, trade_time);
