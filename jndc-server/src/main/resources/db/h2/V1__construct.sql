-- ----------------------------
-- Table structure for channel_open
-- ----------------------------
DROP TABLE IF EXISTS channel_open;
CREATE TABLE channel_open
(
    id            BIGINT       NOT NULL,
    ndc_client_id VARCHAR(255) NULL DEFAULT NULL,
    ndc_server_id VARCHAR(255) NULL DEFAULT NULL,
    create_date   DATETIME     NULL DEFAULT NULL,
    update_date   DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- ----------------------------
-- Table structure for ndc_server
-- ----------------------------
DROP TABLE IF EXISTS ndc_server;
CREATE TABLE ndc_server
(
    id           BIGINT       NOT NULL,
    server_name  VARCHAR(255) NULL DEFAULT NULL,
    listen_port  INT          NULL DEFAULT NULL,
    server_state VARCHAR(255) NULL DEFAULT NULL,
    create_date  DATETIME     NULL DEFAULT NULL,
    update_date  DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- ----------------------------
-- Table structure for plugin_ip_record
-- ----------------------------
DROP TABLE IF EXISTS plugin_ip_record;
CREATE TABLE plugin_ip_record
(
    id               BIGINT       NOT NULL,
    ip               VARCHAR(255) NULL DEFAULT NULL,
    last_active_time DATETIME     NULL DEFAULT NULL,
    total_traffic    BIGINT       NULL DEFAULT NULL,
    create_date      DATETIME     NULL DEFAULT NULL,
    update_date      DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- ----------------------------
-- Table structure for plugin_time_range
-- ----------------------------
DROP TABLE IF EXISTS plugin_time_range;
CREATE TABLE plugin_time_range
(
    range_id    BIGINT   NOT NULL,
    range_start TIME     NULL DEFAULT NULL,
    range_end   TIME     NULL DEFAULT NULL,
    create_date DATETIME NULL DEFAULT NULL,
    update_date DATETIME NULL DEFAULT NULL,
    PRIMARY KEY (range_id)
);

-- ----------------------------
-- Table structure for pure_meta
-- ----------------------------
DROP TABLE IF EXISTS pure_meta;
CREATE TABLE pure_meta
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NULL DEFAULT NULL,
    icon        VARCHAR(255) NULL DEFAULT NULL,
    rank_number        INT          NULL DEFAULT NULL,
    roles       VARCHAR(255) NULL DEFAULT NULL,
    auths       VARCHAR(255) NULL DEFAULT NULL,
    create_time DATETIME     NULL DEFAULT NULL,
    update_time DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- ----------------------------
-- Table structure for pure_permission
-- ----------------------------
DROP TABLE IF EXISTS pure_permission;
CREATE TABLE pure_permission
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    path        VARCHAR(255) NULL DEFAULT NULL,
    create_time DATETIME     NULL DEFAULT NULL,
    update_time DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- ----------------------------
-- Table structure for pure_route
-- ----------------------------
DROP TABLE IF EXISTS pure_route;
CREATE TABLE pure_route
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    path        VARCHAR(255) NULL DEFAULT NULL,
    name        VARCHAR(255) NULL DEFAULT NULL,
    component   VARCHAR(255) NULL DEFAULT NULL,
    pure_meta   VARCHAR(255) NULL DEFAULT NULL,
    create_time DATETIME     NULL DEFAULT NULL,
    update_time DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);

-- ----------------------------
-- Table structure for pure_user
-- ----------------------------
DROP TABLE IF EXISTS pure_user;
CREATE TABLE pure_user
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    avatar        VARCHAR(255) NULL DEFAULT NULL,
    username      VARCHAR(255) NULL DEFAULT NULL,
    password      VARCHAR(255) NULL DEFAULT NULL,
    nickname      VARCHAR(255) NULL DEFAULT NULL,
    roles         VARCHAR(255) NULL DEFAULT NULL,
    permissions   VARCHAR(255) NULL DEFAULT NULL,
    access_token  VARCHAR(255) NULL DEFAULT NULL,
    refresh_token VARCHAR(255) NULL DEFAULT NULL,
    expires       VARCHAR(255) NULL DEFAULT NULL,
    create_date   DATETIME     NULL DEFAULT NULL,
    update_date   DATETIME     NULL DEFAULT NULL,
    PRIMARY KEY (id)
);
