CREATE TABLE IF NOT EXISTS quant_account (
    id                BIGINT         NOT NULL,
    account_id        VARCHAR(64)    NOT NULL,
    account_name      VARCHAR(128),
    account_type      VARCHAR(32),
    status            VARCHAR(16),
    available_balance DECIMAL(20, 6) NOT NULL DEFAULT 0,
    frozen_balance    DECIMAL(20, 6) NOT NULL DEFAULT 0,
    create_time       TIMESTAMP      NOT NULL,
    update_time       TIMESTAMP      NOT NULL,
    CONSTRAINT pk_quant_account PRIMARY KEY (id),
    CONSTRAINT uk_quant_account_account_id UNIQUE (account_id)
);

COMMENT ON TABLE quant_account IS '量化账户表';
COMMENT ON COLUMN quant_account.id IS '主键, 雪花ID';
COMMENT ON COLUMN quant_account.account_id IS '账户编号';
COMMENT ON COLUMN quant_account.account_name IS '账户名称';
COMMENT ON COLUMN quant_account.account_type IS '账户类型';
COMMENT ON COLUMN quant_account.status IS '账户状态';
COMMENT ON COLUMN quant_account.available_balance IS '可用余额';
COMMENT ON COLUMN quant_account.frozen_balance IS '冻结余额';
COMMENT ON COLUMN quant_account.create_time IS '创建时间';
COMMENT ON COLUMN quant_account.update_time IS '更新时间';

INSERT INTO quant_account (id, account_id, account_name, account_type, status,
                           available_balance, frozen_balance, create_time, update_time)
VALUES (1, 'ACC001', 'Test Account 1', 'INDIVIDUAL', 'ACTIVE',
        1000000.000000, 0.000000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
