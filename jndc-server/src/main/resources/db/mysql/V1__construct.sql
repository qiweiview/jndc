DROP TABLE IF EXISTS `channel_open`;
CREATE TABLE `channel_open`  (
                                 `id` bigint(20) NOT NULL,
                                 `ndc_client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端id',
                                 `ndc_server_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务端id',
                                 `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                 `update_date` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '业务-通道' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for ndc_server
-- ----------------------------
DROP TABLE IF EXISTS `ndc_server`;
CREATE TABLE `ndc_server`  (
                               `id` bigint(20) NOT NULL,
                               `server_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务名称',
                               `listen_port` int(11) NULL DEFAULT NULL COMMENT '监听端口',
                               `server_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务状态',
                               `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_date` datetime NULL DEFAULT NULL COMMENT '修改时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '业务-服务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for plugin_ip_record
-- ----------------------------
DROP TABLE IF EXISTS `plugin_ip_record`;
CREATE TABLE `plugin_ip_record`  (
                                     `id` bigint(20) NOT NULL,
                                     `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip地址',
                                     `last_active_time` datetime NULL DEFAULT NULL COMMENT '最后活跃时间',
                                     `total_traffic` bigint(20) NULL DEFAULT NULL COMMENT '总访问量',
                                     `create_date` datetime NULL DEFAULT NULL COMMENT '创建日期',
                                     `update_date` datetime NULL DEFAULT NULL COMMENT '更新日期',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '插件-ip访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for plugin_time_range
-- ----------------------------
DROP TABLE IF EXISTS `plugin_time_range`;
CREATE TABLE `plugin_time_range`  (
                                      `range_id` bigint(20) NOT NULL,
                                      `range_start` time NULL DEFAULT NULL COMMENT '起始时间',
                                      `range_end` time NULL DEFAULT NULL COMMENT '结束时间',
                                      `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                      `update_date` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                      PRIMARY KEY (`range_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '插件-时间控制' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pure_meta
-- ----------------------------
DROP TABLE IF EXISTS `pure_meta`;
CREATE TABLE `pure_meta`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
                              `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
                              `rank_number` int(11) NULL DEFAULT NULL COMMENT '优先级',
                              `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '允许角色',
                              `auths` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '允许权限',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理-元数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pure_permission
-- ----------------------------
DROP TABLE IF EXISTS `pure_permission`;
CREATE TABLE `pure_permission`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理-权限' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pure_route
-- ----------------------------
DROP TABLE IF EXISTS `pure_route`;
CREATE TABLE `pure_route`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
                               `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件',
                               `pure_meta` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '元数据',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理-路由' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pure_user
-- ----------------------------
DROP TABLE IF EXISTS `pure_user`;
CREATE TABLE `pure_user`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
                              `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
                              `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
                              `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
                              `roles` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
                              `permissions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限集合',
                              `access_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'token',
                              `refresh_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '刷新token',
                              `expires` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '过期时间',
                              `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_date` datetime NULL DEFAULT NULL COMMENT '修改时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理-用户表' ROW_FORMAT = Dynamic;
