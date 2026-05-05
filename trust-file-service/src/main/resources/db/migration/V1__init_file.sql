CREATE TABLE IF NOT EXISTS sys_file_record (
    id            BIGINT       NOT NULL,
    file_id       VARCHAR(64)  NOT NULL,
    file_name     VARCHAR(256) NOT NULL,
    original_name VARCHAR(256),
    file_size     BIGINT,
    file_type     VARCHAR(64),
    storage_type  VARCHAR(32)  NOT NULL,
    storage_path  VARCHAR(512),
    url           VARCHAR(512),
    create_by     VARCHAR(64),
    create_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sys_file_record PRIMARY KEY (id),
    CONSTRAINT uk_sys_file_record_file_id UNIQUE (file_id)
);

COMMENT ON TABLE sys_file_record IS '文件记录表';
COMMENT ON COLUMN sys_file_record.id IS '主键, 雪花ID';
COMMENT ON COLUMN sys_file_record.file_id IS '文件业务ID';
COMMENT ON COLUMN sys_file_record.file_name IS '存储文件名';
COMMENT ON COLUMN sys_file_record.original_name IS '原始文件名';
COMMENT ON COLUMN sys_file_record.file_size IS '文件字节数';
COMMENT ON COLUMN sys_file_record.file_type IS '文件类型';
COMMENT ON COLUMN sys_file_record.storage_type IS '存储类型';
COMMENT ON COLUMN sys_file_record.storage_path IS '存储路径';
COMMENT ON COLUMN sys_file_record.url IS '访问URL';
COMMENT ON COLUMN sys_file_record.create_by IS '创建人';
COMMENT ON COLUMN sys_file_record.create_time IS '创建时间';
