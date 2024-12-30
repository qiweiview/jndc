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
    bind_host     VARCHAR(255) NULL DEFAULT NULL,
    server_remark VARCHAR(255) NULL DEFAULT NULL,
    unique_id     VARCHAR(36) NULL DEFAULT NULL,  -- 同上
    PRIMARY KEY (id)                              -- 直接指定主键，不需要 `USING BTREE`
);


DROP TABLE IF EXISTS jndc_log;
CREATE TABLE jndc_log
(
    id          BIGINT NOT NULL,
    log_content CLOB NULL DEFAULT NULL,
    log_time    TIMESTAMP NULL DEFAULT NULL,
    log_type    VARCHAR(255) NULL DEFAULT NULL,
    source_id   BIGINT NULL DEFAULT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX idx_source ON jndc_log (source_id);


DROP TABLE IF EXISTS jndc_client;
CREATE TABLE jndc_client
(
    id                  BIGINT NOT NULL,           -- 移除 COMMENT 部分
    create_time         TIMESTAMP    DEFAULT NULL, -- 将 datetime 改为 TIMESTAMP
    update_time         TIMESTAMP    DEFAULT NULL, -- 同上
    client_name         VARCHAR(255) DEFAULT NULL, -- 移除 CHARACTER SET 和 COLLATE
    client_status       VARCHAR(255) DEFAULT NULL, -- 同上
    server_host         VARCHAR(255) DEFAULT NULL, -- 同上
    server_port         INT          DEFAULT NULL,
    disguised_protocol  VARCHAR(255) DEFAULT NULL, -- 同上
    unique_id           VARCHAR(36)  DEFAULT NULL, -- 同上
    client_remark       VARCHAR(255) DEFAULT NULL, -- 同上
    auto_reconnect      INT          DEFAULT NULL, -- 同上
    reconnect_max_times INT          DEFAULT NULL, -- 同上
    reconnect_interval  INT          DEFAULT NULL, -- 同上
    PRIMARY KEY (id)                               -- 直接指定主键，不需要 `USING BTREE`                        -- 直接指定主键，不需要 `USING BTREE`
);


DROP TABLE IF EXISTS jndc_server_accept_history;
CREATE TABLE jndc_server_accept_history
(
    id             BIGINT NOT NULL,           -- 移除 COMMENT 部分
    create_time    TIMESTAMP    DEFAULT NULL, -- 将 datetime 改为 TIMESTAMP
    update_time    TIMESTAMP    DEFAULT NULL, -- 同上
    server_id      BIGINT       DEFAULT NULL, -- 同上
    client_id      VARCHAR(36)  DEFAULT NULL, -- 移除 CHARACTER SET 和 COLLATE
    connect_time   TIMESTAMP    DEFAULT NULL, -- 同上
    interrupt_time TIMESTAMP    DEFAULT NULL, -- 同上
    source_ip      VARCHAR(255) DEFAULT NULL, -- 移除 CHARACTER SET 和 COLLATE
    source_port    INT          DEFAULT NULL,
    PRIMARY KEY (id)                          -- 直接指定主键，不需要 `USING BTREE`
);
CREATE INDEX idx_server ON jndc_server_accept_history (server_id);

DROP TABLE IF EXISTS jndc_client_service;
CREATE TABLE jndc_client_service
(
    id                BIGINT NOT NULL,           -- 移除 COMMENT 部分
    create_time       TIMESTAMP    DEFAULT NULL, -- 将 datetime 改为 TIMESTAMP
    update_time       TIMESTAMP    DEFAULT NULL, -- 同上
    client_id         BIGINT       DEFAULT NULL, -- 同上
    service_name      VARCHAR(255) DEFAULT NULL, -- 移除 CHARACTER SET 和 COLLATE
    service_host      VARCHAR(255) DEFAULT NULL, -- 同上
    service_port      INT          DEFAULT NULL,
    expect_port       INT          DEFAULT NULL,
    service_status    VARCHAR(255) DEFAULT NULL, -- 同上
    auto_register     INT          DEFAULT NULL, -- 同上
    service_protocol  VARCHAR(255) DEFAULT NULL, -- 同上
    service_mode      VARCHAR(255) DEFAULT NULL, -- 同上
    service_unique_id VARCHAR(255) DEFAULT NULL, -- 同上
    PRIMARY KEY (id)                             -- 直接指定主键，不需要 `USING BTREE`
);

DROP TABLE IF EXISTS jndc_access_history;
CREATE TABLE jndc_access_history
(
    id               BIGINT NOT NULL,           -- 移除 COMMENT 部分
    create_time      TIMESTAMP    DEFAULT NULL, -- 将 datetime 改为 TIMESTAMP
    remote_ip        VARCHAR(255) DEFAULT NULL, -- 移除 CHARACTER SET 和 COLLATE
    remote_port      INT          DEFAULT NULL,
    destination      VARCHAR(255) DEFAULT NULL, -- 同上
    destination_id   BIGINT       DEFAULT NULL,
    package_sampling CLOB         DEFAULT NULL, -- 将 text 改为 CLOB 类型
    PRIMARY KEY (id)                            -- 直接指定主键，不需要 `USING BTREE`
);

DROP TABLE IF EXISTS jndc_server_app;
CREATE TABLE jndc_server_app
(
    id                BIGINT NOT NULL,
    create_time       TIMESTAMP    DEFAULT NULL,
    server_id         BIGINT       DEFAULT NULL,
    bind_host         VARCHAR(255) DEFAULT NULL,
    bind_port         INT DEFAULT NULL,
    bind_status       VARCHAR(255) DEFAULT NULL,
    source_client_id  VARCHAR(255) DEFAULT NULL,
    source_service_id VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
);

