DROP TABLE IF EXISTS sys_user_wechat;
CREATE TABLE sys_user_wechat
(
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    username        VARCHAR(255),
    email           VARCHAR(50),
    phone           VARCHAR(50),
    nickname        VARCHAR(100)  NOT NULL,
    avatar          VARCHAR(1000) NOT NULL,
    intro           VARCHAR(250),
    gender          TINYINT       NOT NULL DEFAULT 0,
    birthday        DATETIME,
    status          TINYINT       NOT NULL DEFAULT 0,
    create_time     DATETIME      NOT NULL,
    create_by       BIGINT,
    update_time     DATETIME,
    update_by       BIGINT,
    PRIMARY KEY (id)
);
