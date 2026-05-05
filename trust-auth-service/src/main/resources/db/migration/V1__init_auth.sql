CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL,
    user_id     VARCHAR(64)  NOT NULL,
    username    VARCHAR(64)  NOT NULL,
    password    VARCHAR(128) NOT NULL,
    dept_id     BIGINT,
    status      VARCHAR(16)  DEFAULT 'ACTIVE',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sys_user PRIMARY KEY (id),
    CONSTRAINT uk_sys_user_user_id UNIQUE (user_id)
);

COMMENT ON TABLE sys_user IS '系统用户';
COMMENT ON COLUMN sys_user.id IS '主键, 雪花ID';
COMMENT ON COLUMN sys_user.user_id IS '用户业务ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码(BCrypt)';
COMMENT ON COLUMN sys_user.dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user.status IS '状态';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';

CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT      NOT NULL,
    role_id     VARCHAR(64) NOT NULL,
    role_name   VARCHAR(64) NOT NULL,
    role_code   VARCHAR(64) NOT NULL,
    create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sys_role PRIMARY KEY (id),
    CONSTRAINT uk_sys_role_role_id UNIQUE (role_id),
    CONSTRAINT uk_sys_role_role_code UNIQUE (role_code)
);

COMMENT ON TABLE sys_role IS '系统角色';
COMMENT ON COLUMN sys_role.id IS '主键, 雪花ID';
COMMENT ON COLUMN sys_role.role_id IS '角色业务ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id          BIGINT      NOT NULL,
    user_id     VARCHAR(64) NOT NULL,
    role_id     VARCHAR(64) NOT NULL,
    create_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sys_user_role PRIMARY KEY (id),
    CONSTRAINT uk_sys_user_role UNIQUE (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户-角色关系';
COMMENT ON COLUMN sys_user_role.id IS '主键, 雪花ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户业务ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色业务ID';
COMMENT ON COLUMN sys_user_role.create_time IS '创建时间';

CREATE TABLE IF NOT EXISTS sys_dept (
    id          BIGINT       NOT NULL,
    dept_id     BIGINT       NOT NULL,
    parent_id   BIGINT,
    dept_name   VARCHAR(64)  NOT NULL,
    ancestors   VARCHAR(256),
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sys_dept PRIMARY KEY (id),
    CONSTRAINT uk_sys_dept_dept_id UNIQUE (dept_id)
);

COMMENT ON TABLE sys_dept IS '部门';
COMMENT ON COLUMN sys_dept.id IS '主键, 雪花ID';
COMMENT ON COLUMN sys_dept.dept_id IS '部门业务ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.ancestors IS '祖级路径';
COMMENT ON COLUMN sys_dept.create_time IS '创建时间';

CREATE TABLE IF NOT EXISTS sys_data_scope (
    id          BIGINT       NOT NULL,
    scope_id    VARCHAR(64)  NOT NULL,
    role_id     VARCHAR(64)  NOT NULL,
    scope_type  VARCHAR(32)  NOT NULL,
    dept_ids    VARCHAR(512),
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sys_data_scope PRIMARY KEY (id),
    CONSTRAINT uk_sys_data_scope_scope_id UNIQUE (scope_id)
);

COMMENT ON TABLE sys_data_scope IS '数据权限规则';
COMMENT ON COLUMN sys_data_scope.id IS '主键, 雪花ID';
COMMENT ON COLUMN sys_data_scope.scope_id IS '权限业务ID';
COMMENT ON COLUMN sys_data_scope.role_id IS '关联角色业务ID';
COMMENT ON COLUMN sys_data_scope.scope_type IS '权限类型: ALL/CUSTOM';
COMMENT ON COLUMN sys_data_scope.dept_ids IS '自定义部门ID列表(逗号分隔)';
COMMENT ON COLUMN sys_data_scope.create_time IS '创建时间';

INSERT INTO sys_user (id, user_id, username, password, dept_id, status, create_time, update_time) VALUES
    (1, 'admin',  'admin',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'user01', 'user01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO sys_role (id, role_id, role_name, role_code, create_time) VALUES
    (1, 'admin', '管理员',   'ROLE_ADMIN', CURRENT_TIMESTAMP),
    (2, 'user',  '普通用户', 'ROLE_USER',  CURRENT_TIMESTAMP);

INSERT INTO sys_user_role (id, user_id, role_id, create_time) VALUES
    (1, 'admin',  'admin', CURRENT_TIMESTAMP),
    (2, 'user01', 'user',  CURRENT_TIMESTAMP);

INSERT INTO sys_dept (id, dept_id, parent_id, dept_name, ancestors, create_time) VALUES
    (1, 1, 0, '总部',       '0',   CURRENT_TIMESTAMP),
    (2, 2, 1, '量化交易部', '0,1', CURRENT_TIMESTAMP),
    (3, 3, 1, '资产管理部', '0,1', CURRENT_TIMESTAMP);

INSERT INTO sys_data_scope (id, scope_id, role_id, scope_type, dept_ids, create_time) VALUES
    (1, '1', 'admin', 'ALL',    NULL, CURRENT_TIMESTAMP),
    (2, '2', 'user',  'CUSTOM', '2',  CURRENT_TIMESTAMP);
