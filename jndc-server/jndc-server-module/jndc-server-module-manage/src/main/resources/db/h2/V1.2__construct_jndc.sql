DROP TABLE IF EXISTS jndc_server;
CREATE TABLE jndc_server
(
    id            BIGINT       NOT NULL COMMENT '主键ID',
    create_time   DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    update_time   DATETIME     NULL DEFAULT NULL COMMENT '更新时间',
    server_name   VARCHAR(255) NULL DEFAULT NULL COMMENT '服务器名称',
    server_status VARCHAR(255) NULL DEFAULT NULL COMMENT '服务器状态',
    bind_tactics  VARCHAR(255) NULL DEFAULT NULL COMMENT '绑定策略',
    bind_port     INT          NULL DEFAULT NULL COMMENT '绑定端口',
    bind_host     VARCHAR(255) NULL DEFAULT NULL COMMENT '绑定主机',
    server_remark VARCHAR(255) NULL DEFAULT NULL COMMENT '服务器备注',
    unique_id     VARCHAR(36)  NULL DEFAULT NULL COMMENT '唯一标识',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务器信息表';

DROP TABLE IF EXISTS jndc_log;
CREATE TABLE jndc_log
(
    id          BIGINT       NOT NULL COMMENT '主键ID',
    log_content TEXT         NULL DEFAULT NULL COMMENT '日志内容',
    log_time    DATETIME     NULL DEFAULT NULL COMMENT '日志时间',
    log_type    VARCHAR(255) NULL DEFAULT NULL COMMENT '日志类型',
    source_id   BIGINT       NULL DEFAULT NULL COMMENT '来源ID',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日志表';
CREATE INDEX idx_source ON jndc_log (source_id) USING BTREE;

DROP TABLE IF EXISTS jndc_client;
CREATE TABLE jndc_client
(
    id                  BIGINT       NOT NULL COMMENT '主键ID',
    create_time         DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    update_time         DATETIME     NULL DEFAULT NULL COMMENT '更新时间',
    client_name         VARCHAR(255) NULL DEFAULT NULL COMMENT '客户端名称',
    client_status       VARCHAR(255) NULL DEFAULT NULL COMMENT '客户端状态',
    server_host         VARCHAR(255) NULL DEFAULT NULL COMMENT '服务器主机',
    server_port         INT          NULL DEFAULT NULL COMMENT '服务器端口',
    disguised_protocol  VARCHAR(255) NULL DEFAULT NULL COMMENT '伪装协议',
    unique_id           VARCHAR(36)  NULL DEFAULT NULL COMMENT '唯一标识',
    client_remark       VARCHAR(255) NULL DEFAULT NULL COMMENT '客户端备注',
    auto_reconnect      INT          NULL DEFAULT NULL COMMENT '自动重连',
    reconnect_max_times INT          NULL DEFAULT NULL COMMENT '最大重连次数',
    reconnect_interval  INT          NULL DEFAULT NULL COMMENT '重连间隔',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户端信息表';

DROP TABLE IF EXISTS jndc_server_accept_history;
CREATE TABLE jndc_server_accept_history
(
    id                    BIGINT       NOT NULL COMMENT '主键ID',
    create_time           DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    update_time           DATETIME     NULL DEFAULT NULL COMMENT '更新时间',
    latest_heart_beat_time DATETIME     NULL DEFAULT NULL COMMENT '最后心跳时间',
    server_id             BIGINT       NULL DEFAULT NULL COMMENT '服务器ID',
    client_id             VARCHAR(36)  NULL DEFAULT NULL COMMENT '客户端ID',
    connect_time          DATETIME     NULL DEFAULT NULL COMMENT '连接时间',
    interrupt_time        DATETIME     NULL DEFAULT NULL COMMENT '中断时间',
    source_ip             VARCHAR(255) NULL DEFAULT NULL COMMENT '源IP',
    source_port           INT          NULL DEFAULT NULL COMMENT '源端口',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务器接受历史表';
CREATE INDEX idx_server ON jndc_server_accept_history (server_id) USING BTREE;

DROP TABLE IF EXISTS jndc_client_service;
CREATE TABLE jndc_client_service
(
    id                BIGINT       NOT NULL COMMENT '主键ID',
    create_time       DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    update_time       DATETIME     NULL DEFAULT NULL COMMENT '更新时间',
    client_id         BIGINT       NULL DEFAULT NULL COMMENT '客户端ID',
    service_name      VARCHAR(255) NULL DEFAULT NULL COMMENT '服务名称',
    service_host      VARCHAR(255) NULL DEFAULT NULL COMMENT '服务主机',
    service_port      INT          NULL DEFAULT NULL COMMENT '服务端口',
    expect_port       INT          NULL DEFAULT NULL COMMENT '期望端口',
    service_status    VARCHAR(255) NULL DEFAULT NULL COMMENT '服务状态',
    auto_register     INT          NULL DEFAULT NULL COMMENT '自动注册',
    service_protocol  VARCHAR(255) NULL DEFAULT NULL COMMENT '服务协议',
    service_mode      VARCHAR(255) NULL DEFAULT NULL COMMENT '服务模式',
    service_unique_id VARCHAR(255) NULL DEFAULT NULL COMMENT '服务唯一标识',
    store_for         VARCHAR(255) NULL DEFAULT NULL COMMENT '存储用途',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户端服务表';

DROP TABLE IF EXISTS jndc_server_service;
CREATE TABLE jndc_server_service
(
    id                BIGINT       NOT NULL COMMENT '主键ID',
    create_time       DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    update_time       DATETIME     NULL DEFAULT NULL COMMENT '更新时间',
    client_unique_id  VARCHAR(255) NULL DEFAULT NULL COMMENT '客户端唯一标识',
    service_name      VARCHAR(255) NULL DEFAULT NULL COMMENT '服务名称',
    service_host      VARCHAR(255) NULL DEFAULT NULL COMMENT '服务主机',
    service_port      INT          NULL DEFAULT NULL COMMENT '服务端口',
    expect_port       INT          NULL DEFAULT NULL COMMENT '期望端口',
    service_status    VARCHAR(255) NULL DEFAULT NULL COMMENT '服务状态',
    service_protocol  VARCHAR(255) NULL DEFAULT NULL COMMENT '服务协议',
    service_mode      VARCHAR(255) NULL DEFAULT NULL COMMENT '服务模式',
    service_unique_id VARCHAR(255) NULL DEFAULT NULL COMMENT '服务唯一标识',
    server_unique_id  VARCHAR(255) NULL DEFAULT NULL COMMENT '服务器唯一标识',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务器服务表';

DROP TABLE IF EXISTS jndc_access_history;
CREATE TABLE jndc_access_history
(
    id              BIGINT       NOT NULL COMMENT '主键ID',
    create_time     DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    remote_ip       VARCHAR(255) NULL DEFAULT NULL COMMENT '远程IP',
    remote_port     INT          NULL DEFAULT NULL COMMENT '远程端口',
    destination     VARCHAR(255) NULL DEFAULT NULL COMMENT '目标地址',
    destination_id  BIGINT       NULL DEFAULT NULL COMMENT '目标ID',
    package_sampling TEXT        NULL DEFAULT NULL COMMENT '包采样',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问历史表';

DROP TABLE IF EXISTS jndc_server_app;
CREATE TABLE jndc_server_app
(
    id                BIGINT       NOT NULL COMMENT '主键ID',
    create_time       DATETIME     NULL DEFAULT NULL COMMENT '创建时间',
    server_id         BIGINT       NULL DEFAULT NULL COMMENT '服务器ID',
    bind_host         VARCHAR(255) NULL DEFAULT NULL COMMENT '绑定主机',
    bind_port         INT          NULL DEFAULT NULL COMMENT '绑定端口',
    bind_status       VARCHAR(255) NULL DEFAULT NULL COMMENT '绑定状态',
    bind_type         VARCHAR(255) NULL DEFAULT NULL COMMENT '绑定类型',
    meta_data         TEXT         NULL DEFAULT NULL COMMENT '元数据',
    source_client_id  VARCHAR(255) NULL DEFAULT NULL COMMENT '源客户端ID',
    source_service_id VARCHAR(255) NULL DEFAULT NULL COMMENT '源服务ID',
    PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务器应用表';

