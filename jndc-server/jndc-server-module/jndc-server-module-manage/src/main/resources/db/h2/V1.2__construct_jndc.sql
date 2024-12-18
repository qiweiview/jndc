DROP TABLE IF EXISTS jndc_server;
CREATE TABLE jndc_server
(
    id            BIGINT NOT NULL,
    create_time   TIMESTAMP NULL DEFAULT NULL,    -- 将 MySQL 的 datetime 改为 H2 的 timestamp
    update_time   TIMESTAMP NULL DEFAULT NULL,    -- 同上
    server_name   VARCHAR(255) NULL DEFAULT NULL, -- 移除 CHARACTER SET 和 COLLATE
    server_status VARCHAR(255) NULL DEFAULT NULL, -- 同上
    bind_tactics  VARCHAR(255) NULL DEFAULT NULL, -- 同上
    bind_port     INT NULL DEFAULT NULL,
    server_remark VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (id)                              -- 直接指定主键，不需要 `USING BTREE`
);

