CREATE TABLE IF NOT EXISTS sys_audit_log (
    id          BIGINT      NOT NULL,
    trace_id    VARCHAR(64),
    module      VARCHAR(64) NOT NULL,
    action      VARCHAR(64) NOT NULL,
    biz_id      VARCHAR(64),
    user_id     VARCHAR(64),
    req_params  TEXT,
    resp_result TEXT,
    status      VARCHAR(16),
    cost_ms     BIGINT,
    ip          VARCHAR(64),
    create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sys_audit_log PRIMARY KEY (id)
);

COMMENT ON TABLE sys_audit_log IS '系统审计日志';
COMMENT ON COLUMN sys_audit_log.id IS '主键, 雪花ID';
COMMENT ON COLUMN sys_audit_log.trace_id IS '链路ID';
COMMENT ON COLUMN sys_audit_log.module IS '业务模块';
COMMENT ON COLUMN sys_audit_log.action IS '操作动作';
COMMENT ON COLUMN sys_audit_log.biz_id IS '业务主键';
COMMENT ON COLUMN sys_audit_log.user_id IS '操作人';
COMMENT ON COLUMN sys_audit_log.req_params IS '请求参数';
COMMENT ON COLUMN sys_audit_log.resp_result IS '响应结果';
COMMENT ON COLUMN sys_audit_log.status IS '执行状态';
COMMENT ON COLUMN sys_audit_log.cost_ms IS '耗时毫秒';
COMMENT ON COLUMN sys_audit_log.ip IS '客户端IP';
COMMENT ON COLUMN sys_audit_log.create_time IS '创建时间';
