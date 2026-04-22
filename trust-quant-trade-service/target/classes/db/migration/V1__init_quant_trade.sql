CREATE TABLE IF NOT EXISTS qt_order (
    id BIGINT PRIMARY KEY,
    request_id VARCHAR(64) NOT NULL,
    account_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    side VARCHAR(8) NOT NULL,
    order_type VARCHAR(16) NOT NULL,
    tif VARCHAR(16) NOT NULL,
    price DECIMAL(20, 6) NOT NULL,
    quantity DECIMAL(20, 6) NOT NULL,
    remaining_quantity DECIMAL(20, 6) NOT NULL,
    status VARCHAR(32) NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS qt_trade (
    id BIGINT PRIMARY KEY,
    buy_order_id BIGINT NOT NULL,
    sell_order_id BIGINT NOT NULL,
    buy_account_id VARCHAR(64) NOT NULL,
    sell_account_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    price DECIMAL(20, 6) NOT NULL,
    quantity DECIMAL(20, 6) NOT NULL,
    trade_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS qt_account (
    id BIGINT PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL,
    available_balance DECIMAL(20, 6) NOT NULL,
    update_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS qt_position (
    id BIGINT PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    quantity DECIMAL(20, 6) NOT NULL,
    update_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS qt_risk_log (
    id BIGINT PRIMARY KEY,
    account_id VARCHAR(64) NOT NULL,
    symbol VARCHAR(32) NOT NULL,
    passed BOOLEAN NOT NULL,
    reason VARCHAR(256),
    create_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS qt_idempotency_log (
    id BIGINT PRIMARY KEY,
    request_id VARCHAR(64) NOT NULL,
    biz_type VARCHAR(32) NOT NULL,
    create_time TIMESTAMP NOT NULL
);
