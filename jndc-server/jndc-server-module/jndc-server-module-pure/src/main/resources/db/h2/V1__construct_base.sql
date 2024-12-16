DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config
(
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    config_name  VARCHAR(100)  NULL     DEFAULT '',
    config_key   VARCHAR(100)  NULL     DEFAULT '',
    config_value VARCHAR(3000) NULL     DEFAULT '',
    create_by    BIGINT        NULL     DEFAULT NULL,
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by    BIGINT        NULL     DEFAULT NULL,
    update_time  DATETIME      NULL     DEFAULT NULL,
    del_flag     TINYINT       NOT NULL DEFAULT 0,
    remark       VARCHAR(500)  NULL     DEFAULT NULL,
    type         TINYINT       NULL     DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    dict_code   VARCHAR(100) NOT NULL DEFAULT '',
    dict_name   VARCHAR(100) NOT NULL,
    status      TINYINT      NULL     DEFAULT 0,
    del_flag    TINYINT      NULL     DEFAULT 0,
    create_by   BIGINT       NULL     DEFAULT NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   BIGINT       NULL     DEFAULT NULL,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remark      VARCHAR(500) NULL     DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    dict_id     BIGINT       NOT NULL,
    name        VARCHAR(100) NULL DEFAULT NULL,
    `value`     VARCHAR(100) NULL DEFAULT NULL,
    sort_order  INT          NULL DEFAULT NULL,
    color       VARCHAR(50)  NULL DEFAULT NULL,
    status      TINYINT      NULL DEFAULT 0,
    create_by   BIGINT       NULL DEFAULT NULL,
    create_time DATETIME     NULL DEFAULT NULL,
    update_by   BIGINT       NULL DEFAULT NULL,
    update_time DATETIME     NULL DEFAULT NULL,
    remark      VARCHAR(500) NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    account        VARCHAR(50)  NULL DEFAULT '',
    ip_address     VARCHAR(128) NULL DEFAULT '',
    login_location VARCHAR(255) NULL DEFAULT '',
    browser        VARCHAR(50)  NULL DEFAULT '',
    os             VARCHAR(50)  NULL DEFAULT '',
    status         TINYINT      NULL DEFAULT 0,
    msg            VARCHAR(255) NULL DEFAULT '',
    login_time     DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(50)  NOT NULL,
    title         VARCHAR(50)  NOT NULL,
    parent_id     BIGINT       NULL     DEFAULT 0,
    sort_order    INT          NULL     DEFAULT 1,
    path          VARCHAR(200) NULL     DEFAULT '',
    component     VARCHAR(255) NULL     DEFAULT NULL,
    `query`      VARCHAR(255) NULL     DEFAULT NULL,
    cache_flag    TINYINT      NOT NULL DEFAULT 0,
    type          TINYINT      NOT NULL DEFAULT 0,
    visible       TINYINT      NULL     DEFAULT 0,
    perms         VARCHAR(100) NULL     DEFAULT NULL,
    icon          VARCHAR(100) NULL     DEFAULT '#',
    create_by     BIGINT       NOT NULL,
    create_time   DATETIME     NOT NULL,
    update_by     BIGINT       NULL     DEFAULT NULL,
    update_time   DATETIME     NULL     DEFAULT NULL,
    remark        VARCHAR(500) NULL     DEFAULT '',
    platform_type TINYINT      NULL     DEFAULT 0,
    redirect      VARCHAR(255) NULL     DEFAULT NULL,
    frame_loading TINYINT      NULL     DEFAULT 0,
    frame_src     VARCHAR(255) NULL     DEFAULT NULL,
    PRIMARY KEY (id)
);
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(50)  NOT NULL,
    content     BLOB         NULL,
    type        TINYINT      NOT NULL,
    status      TINYINT      NULL     DEFAULT 0,
    create_by   BIGINT       NULL     DEFAULT NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by   BIGINT       NULL     DEFAULT NULL,
    update_time DATETIME     NULL     DEFAULT NULL,
    del_flag    TINYINT      NOT NULL DEFAULT 0,
    remark      VARCHAR(255) NULL     DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_notice_role;
CREATE TABLE sys_notice_role
(
    id        BIGINT NOT NULL AUTO_INCREMENT,
    notice_id BIGINT NOT NULL,
    role_id   BIGINT NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_notice_user_read;
CREATE TABLE sys_notice_user_read
(
    id          BIGINT     NOT NULL AUTO_INCREMENT,
    notice_id   BIGINT     NOT NULL,
    user_id     BIGINT     NOT NULL,
    status      TINYINT NOT NULL DEFAULT 0,
    read_time   DATETIME   NULL     DEFAULT NULL,
    create_time DATETIME   NOT NULL,
    update_time DATETIME   NULL     DEFAULT NULL,
    del_flag    TINYINT    NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log
(
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    title          VARCHAR(50)   NULL DEFAULT '',
    business_type  TINYINT       NULL DEFAULT 0,
    method         VARCHAR(100)  NULL DEFAULT '',
    request_method VARCHAR(10)   NULL DEFAULT '',
    operator_type  TINYINT    NULL DEFAULT 0,
    oper_username  VARCHAR(255)  NULL DEFAULT NULL,
    oper_url       VARCHAR(255)  NULL DEFAULT '',
    oper_ip        VARCHAR(128)  NULL DEFAULT '',
    oper_location  VARCHAR(255)  NULL DEFAULT '',
    oper_param     VARCHAR(2000) NULL DEFAULT '',
    json_result    VARCHAR(2000) NULL DEFAULT '',
    status         TINYINT    NULL DEFAULT 0,
    error_msg      VARCHAR(2000) NULL DEFAULT '',
    oper_time      DATETIME      NULL DEFAULT NULL,
    cost_time      BIGINT        NULL DEFAULT 0,
    PRIMARY KEY (id)

);

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    role_name   VARCHAR(30)  NOT NULL,
    role_code   VARCHAR(100) NOT NULL,
    status      TINYINT   NOT NULL DEFAULT 0,
    del_flag    TINYINT   NULL     DEFAULT 0,
    create_by   BIGINT       NULL     DEFAULT NULL,
    create_time DATETIME     NULL     DEFAULT NULL,
    update_by   BIGINT       NULL     DEFAULT NULL,
    update_time DATETIME     NULL     DEFAULT NULL,
    remark      VARCHAR(500) NULL     DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu
(
    id      BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);


DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user
(
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    we_chat_id        VARCHAR(255),
    username        VARCHAR(255),
    password        VARCHAR(255)  NOT NULL,
    email           VARCHAR(50),
    phone           VARCHAR(50),
    nickname        VARCHAR(100),
    avatar          VARCHAR(1000),
    intro           VARCHAR(250),
    gender          TINYINT DEFAULT 0,
    birthday        DATETIME,
    status          TINYINT DEFAULT 0,
    create_time     DATETIME,
    create_by       BIGINT,
    update_time     DATETIME,
    update_by       BIGINT,
    register_source VARCHAR(50),
    del_flag        TINYINT                DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role
(
    id      BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);



