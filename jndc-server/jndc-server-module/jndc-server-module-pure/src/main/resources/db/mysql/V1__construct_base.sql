-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `id`           int      NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数名称',
    `config_key`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键名',
    `config_value` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键值',
    `create_by`    bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间',
    `del_flag`     tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0否1是',
    `remark`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
    `type`         tinyint(1) NULL DEFAULT 0 COMMENT '是否系统内置（0否1是）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `dict_code`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字典编码',
    `dict_name`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字典名称',
    `status`      tinyint(1) NULL DEFAULT 0 COMMENT '状态（0开启 1关闭）',
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    `create_by`   bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_sd_dict_code`(`dict_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_id`     bigint NOT NULL COMMENT '字典ID',
    `name`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据项名称',
    `value`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据项值',
    `sort_order`  int NULL DEFAULT NULL COMMENT '排序',
    `color`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '颜色值',
    `status`      tinyint(1) NULL DEFAULT 0 COMMENT '状态（0开启 1关闭）',
    `create_by`   bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`
(
    `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `account`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户账号',
    `ip_address`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录IP地址',
    `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录地点',
    `browser`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '浏览器类型',
    `os`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作系统',
    `status`         tinyint(1) NULL DEFAULT 0 COMMENT '登录状态（0成功 1失败）',
    `msg`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '提示消息',
    `login_time`     datetime NULL DEFAULT NULL COMMENT '登录时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `idx_sys_login_log_s`(`status` ASC) USING BTREE,
    INDEX            `idx_sys_login_log_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 177 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `id`            bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `name`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由名称',
    `title`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
    `parent_id`     bigint NULL DEFAULT 0 COMMENT '父菜单ID',
    `sort_order`    int NULL DEFAULT 1 COMMENT '显示顺序',
    `path`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
    `component`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
    `query`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
    `cache_flag`    tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否缓存（0不缓存 1缓存）',
    `type`          tinyint(1) NOT NULL DEFAULT 0 COMMENT '菜单类型（0目录 1菜单 2iframe 3外链 4按钮）',
    `visible`       tinyint(1) NULL DEFAULT 0 COMMENT '菜单状态（0显示 1隐藏）',
    `perms`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
    `icon`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
    `create_by`     bigint                                                       NOT NULL COMMENT '创建者',
    `create_time`   datetime                                                     NOT NULL COMMENT '创建时间',
    `update_by`     bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time`   datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
    `platform_type` tinyint NULL DEFAULT 0 COMMENT '菜单所属平台（0后台 1前台）',
    `redirect`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由重定向',
    `frame_loading` tinyint(1) NULL DEFAULT 0 COMMENT '内嵌的iframe页面是否开启首次加载动画（0否 1是）',
    `frame_src`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'iframe页面地址',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 88 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`
(
    `id`          bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
    `content`     longblob NULL COMMENT '公告内容',
    `type`        tinyint(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
    `status`      tinyint(1) NULL DEFAULT 0 COMMENT '公告状态（0正常 1关闭）',
    `create_by`   bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `del_flag`    tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 0否1是',
    `remark`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_role`;
CREATE TABLE `sys_notice_role`
(
    `id`        bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `notice_id` bigint NOT NULL COMMENT '通知ID，只有在通知公告类型为通知时才存',
    `role_id`   bigint NOT NULL COMMENT '要通知的角色',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_role
-- ----------------------------

-- ----------------------------
-- Table structure for sys_notice_user_read
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_user_read`;
CREATE TABLE `sys_notice_user_read`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `notice_id`   bigint   NOT NULL COMMENT '通知公告ID',
    `user_id`     bigint   NOT NULL COMMENT '用户ID',
    `status`      tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读（0否1是）',
    `read_time`   datetime NULL DEFAULT NULL COMMENT '已读时间',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `del_flag`    tinyint  NOT NULL DEFAULT 0 COMMENT '是否删除（0否1是）',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_user_notice`(`user_id` ASC, `notice_id` ASC) USING BTREE,
    INDEX         `idx_user_notice`(`user_id` ASC, `notice_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户公告通知已读状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice_user_read
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`
(
    `id`             bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `title`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '模块标题',
    `business_type`  tinyint NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '方法名称',
    `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求方式',
    `operator_type`  tinyint(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2前台用户）',
    `oper_username`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作用户名',
    `oper_url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求URL',
    `oper_ip`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '主机地址',
    `oper_location`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作地点',
    `oper_param`     varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求参数',
    `json_result`    varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '返回参数',
    `status`         tinyint(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    `error_msg`      varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '错误消息',
    `oper_time`      datetime NULL DEFAULT NULL COMMENT '操作时间',
    `cost_time`      bigint NULL DEFAULT 0 COMMENT '消耗时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX            `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
    INDEX            `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
    INDEX            `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志记录表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '角色名称',
    `role_code`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
    `status`      tinyint(1) NOT NULL DEFAULT 0 COMMENT '角色状态（0正常 1停用）',
    `del_flag`    tinyint(1) NULL DEFAULT 0 COMMENT '删除标志（0否 1是）',
    `create_by`   bigint NULL DEFAULT NULL COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint NULL DEFAULT NULL COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uniq_sys_role_role_code`(`role_code` ASC) USING BTREE,
    INDEX         `idx_sr_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `id`      bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 157 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`              bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键（用户id）',
    `username`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名（登录名）',
    `password`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '密码',
    `email`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱号',
    `phone`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
    `nickname`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '昵称',
    `avatar`          varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '头像',
    `intro`           varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '简介',
    `gender`          tinyint(1) NOT NULL DEFAULT 0 COMMENT '性别（0未知，1男，2女）',
    `birthday`        datetime NULL DEFAULT NULL COMMENT '出生日期',
    `status`          tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态（0正常，1禁用）',
    `create_time`     datetime                                                       NOT NULL COMMENT '创建时间',
    `create_by`       bigint NULL DEFAULT NULL COMMENT '创建人',
    `update_time`     datetime NULL DEFAULT NULL COMMENT '更新时间',
    `update_by`       bigint NULL DEFAULT NULL COMMENT '更新人',
    `register_source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '注册来源',
    `del_flag`        tinyint(1) NULL DEFAULT 0 COMMENT '是否删除（0否1是）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `id`      bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;
