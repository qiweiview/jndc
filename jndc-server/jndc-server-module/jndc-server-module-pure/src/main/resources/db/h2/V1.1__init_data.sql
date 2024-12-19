-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 'System', '系统管理', 0, 1, '/system', '', NULL, 0, 0, 0, '', 'ep:brush-filled', 1, '2024-06-04 10:37:13', 1, '2024-08-22 11:12:15', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (1867209453162561538, 'SystemMonitor', '系统监控', 0, 2, '/monitor', '', NULL, 0, 0, 0, '', 'ri:notification-3-fill', 1, '2024-07-02 20:12:30', 1, '2024-08-21 10:44:52', '', 0, '', 0, '');

-- 系统管理
INSERT INTO `sys_menu` VALUES (3, 'SystemMenu', '菜单管理', 1, 2, '/system/menu', 'system/menu/index', NULL, 0, 1, 0, NULL, 'ep:menu', 1, '2024-06-05 10:07:13', 1, '2024-08-04 20:05:47', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (8, 'SysRole', '角色管理', 1, 3, '/system/role', 'system/role/index', NULL, 0, 1, 0, '', 'fa-solid:user-injured', 1, '2024-07-01 20:20:07', 1, '2024-08-21 10:45:17', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (9, 'SysUser', '用户管理', 1, 1, '/system/user', 'system/user/index', NULL, 0, 1, 0, '', 'ep:avatar', 1, '2024-07-02 10:32:45', 1, '2024-08-21 10:45:03', '', 0, '', 0, '');

-- 系统监控
INSERT INTO `sys_menu` VALUES (87, 'OperationLog', '操作日志', 1867209453162561538, 1, '/monitor/operation', 'system/log/operation/index', NULL, 0, 1, 0, '', 'ep:bicycle', 1, '2024-09-13 09:39:56', NULL, NULL, '', 0, '', 0, '');
-- INSERT INTO `sys_menu` VALUES (88, 'FlowLimit', '流量控制', 1867209453162561538, 2, '/monitor/flow_control', 'monitor/flow_control/index', NULL, 0, 1, 0, '', 'ep:data-line', 1, '2024-09-13 09:39:56', NULL, NULL, '', 0, '', 0, '');


-- 角色管理
INSERT INTO `sys_menu` VALUES (12, '', '角色查询', 8, 1, '', '', NULL, 0, 4, 0, 'system:role:query', '', 1, '2024-07-02 20:03:25', 1, '2024-08-21 10:03:16', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (22, '', '角色新增', 8, 1, '', '', NULL, 0, 4, 0, 'system:role:create', '', 1, '2024-08-21 10:03:51', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (23, '', '角色修改', 8, 1, '', '', NULL, 0, 4, 0, 'system:role:update', '', 1, '2024-08-21 10:19:51', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (24, '', '角色删除', 8, 1, '', '', NULL, 0, 4, 0, 'system:role:delete', '', 1, '2024-08-21 10:20:26', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (25, '', '菜单授予', 8, 1, '', '', NULL, 0, 4, 0, 'system:permission:assignMenu', '', 1, '2024-08-21 10:21:26', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (26, '', '角色所分配菜单查询', 8, 1, '', '', NULL, 0, 4, 0, 'system:permission:getMenus', '', 1, '2024-08-21 10:25:41', 1, '2024-08-21 16:33:40', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (60, '', '查询所有简单角色', 8, 1, '', '', NULL, 0, 4, 0, 'system:role:listSimpleAll', '', 1, '2024-08-21 16:32:00', NULL, NULL, '', 0, '', 0, '');

-- 菜单管理
INSERT INTO `sys_menu` VALUES (18, '', '菜单查询', 3, 1, '', '', NULL, 0, 4, 0, 'system:menu:query', '', 1, '2024-08-18 20:37:54', 1, '2024-08-18 20:45:03', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (19, '', '菜单新增', 3, 1, '', '', NULL, 0, 4, 0, 'system:menu:create', '', 1, '2024-08-18 20:38:29', 1, '2024-08-18 21:05:39', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (20, '', '菜单修改', 3, 3, '', '', NULL, 0, 4, 0, 'system:menu:update', '', 1, '2024-08-18 20:47:04', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (21, '', '菜单删除', 3, 4, '', '', NULL, 0, 4, 0, 'system:menu:delete', '', 1, '2024-08-18 20:47:36', NULL, NULL, '', 0, '', 0, '');

-- 用户管理
INSERT INTO `sys_menu` VALUES (27, '', '用户查询', 9, 1, '', '', NULL, 0, 4, 0, 'system:user:query', '', 1, '2024-08-21 10:48:57', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (28, '', '用户新增', 9, 1, '', '', NULL, 0, 4, 0, 'system:user:create', '', 1, '2024-08-21 10:50:32', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (29, '', '用户修改', 9, 1, '', '', NULL, 0, 4, 0, 'system:user:update', '', 1, '2024-08-21 10:52:58', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (30, '', '用户删除', 9, 1, '', '', NULL, 0, 4, 0, 'system:user:delete', '', 1, '2024-08-21 10:53:59', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (31, '', '密码重置', 9, 1, '', '', NULL, 0, 4, 0, 'system:user:resetPwd', '', 1, '2024-08-21 10:57:14', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (32, '', '为用户分配角色', 9, 1, '', '', NULL, 0, 4, 0, 'system:permission:assignRole', '', 1, '2024-08-21 10:58:40', 1, '2024-08-21 16:17:45', '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (56, '', '用户详情', 9, 1, '', '', NULL, 0, 4, 0, 'system:user:detail', '', 1, '2024-08-21 16:03:04', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (57, '', '用户状态修改', 9, 1, '', '', NULL, 0, 4, 0, 'system:user:updateStatus', '', 1, '2024-08-21 16:08:45', NULL, NULL, '', 0, '', 0, '');
INSERT INTO `sys_menu` VALUES (58, '', '获取用户所分配角色', 9, 1, '', '', NULL, 0, 4, 0, 'system:permission:getRoles', '', 1, '2024-08-21 16:18:51', 1, '2024-08-21 16:19:06', '', 0, '', 0, '');

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'super-admin', 0, 0, 1, '2024-05-09 15:00:27', 1, '2024-07-01 22:41:18', '拥有一切权限');
INSERT INTO `sys_role` VALUES (2, '管理员', 'admin', 0, 0, 1, '2024-05-09 15:03:17', 1, '2024-08-21 10:08:44', '可以进入后台');
INSERT INTO `sys_role` VALUES (3, '普通用户', 'user', 0, 0, 1, '2024-05-09 15:07:49', 1, '2024-08-21 10:09:49', '普通用户');


-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1);
INSERT INTO `sys_role_menu` VALUES (3, 1, 3);
INSERT INTO `sys_role_menu` VALUES (4, 1, 4);
INSERT INTO `sys_role_menu` VALUES (8, 1, 8);
INSERT INTO `sys_role_menu` VALUES (9, 1, 9);
INSERT INTO `sys_role_menu` VALUES (46, 1, 15);
INSERT INTO `sys_role_menu` VALUES (47, 1, 16);
INSERT INTO `sys_role_menu` VALUES (48, 1, 17);
INSERT INTO `sys_role_menu` VALUES (63, 2, 16);
INSERT INTO `sys_role_menu` VALUES (64, 2, 1);
INSERT INTO `sys_role_menu` VALUES (65, 2, 17);
INSERT INTO `sys_role_menu` VALUES (66, 2, 3);
INSERT INTO `sys_role_menu` VALUES (67, 2, 4);
INSERT INTO `sys_role_menu` VALUES (68, 2, 8);
INSERT INTO `sys_role_menu` VALUES (69, 2, 9);
INSERT INTO `sys_role_menu` VALUES (70, 2, 15);
INSERT INTO `sys_role_menu` VALUES (71, 1, 14);
INSERT INTO `sys_role_menu` VALUES (72, 1, 13);
INSERT INTO `sys_role_menu` VALUES (73, 2, 13);
INSERT INTO `sys_role_menu` VALUES (74, 1, 18);
INSERT INTO `sys_role_menu` VALUES (75, 1, 19);
INSERT INTO `sys_role_menu` VALUES (76, 1, 20);
INSERT INTO `sys_role_menu` VALUES (77, 1, 21);
INSERT INTO `sys_role_menu` VALUES (78, 2, 18);
INSERT INTO `sys_role_menu` VALUES (82, 1, 12);
INSERT INTO `sys_role_menu` VALUES (83, 1, 22);
INSERT INTO `sys_role_menu` VALUES (84, 1, 23);
INSERT INTO `sys_role_menu` VALUES (85, 1, 24);
INSERT INTO `sys_role_menu` VALUES (86, 1, 25);
INSERT INTO `sys_role_menu` VALUES (87, 1, 26);
INSERT INTO `sys_role_menu` VALUES (88, 1, 27);
INSERT INTO `sys_role_menu` VALUES (89, 1, 28);
INSERT INTO `sys_role_menu` VALUES (90, 1, 29);
INSERT INTO `sys_role_menu` VALUES (91, 1, 30);
INSERT INTO `sys_role_menu` VALUES (92, 1, 31);
INSERT INTO `sys_role_menu` VALUES (93, 1, 32);
INSERT INTO `sys_role_menu` VALUES (94, 1, 33);
INSERT INTO `sys_role_menu` VALUES (95, 1, 34);
INSERT INTO `sys_role_menu` VALUES (96, 1, 35);
INSERT INTO `sys_role_menu` VALUES (97, 1, 36);
INSERT INTO `sys_role_menu` VALUES (98, 1, 37);
INSERT INTO `sys_role_menu` VALUES (99, 1, 38);
INSERT INTO `sys_role_menu` VALUES (100, 1, 39);
INSERT INTO `sys_role_menu` VALUES (101, 1, 40);
INSERT INTO `sys_role_menu` VALUES (102, 1, 41);
INSERT INTO `sys_role_menu` VALUES (103, 1, 42);
INSERT INTO `sys_role_menu` VALUES (104, 1, 43);
INSERT INTO `sys_role_menu` VALUES (105, 1, 44);
INSERT INTO `sys_role_menu` VALUES (106, 1, 45);
INSERT INTO `sys_role_menu` VALUES (107, 1, 46);
INSERT INTO `sys_role_menu` VALUES (108, 1, 47);
INSERT INTO `sys_role_menu` VALUES (109, 1, 48);
INSERT INTO `sys_role_menu` VALUES (110, 1, 49);
INSERT INTO `sys_role_menu` VALUES (111, 1, 50);
INSERT INTO `sys_role_menu` VALUES (112, 1, 51);
INSERT INTO `sys_role_menu` VALUES (113, 1, 52);
INSERT INTO `sys_role_menu` VALUES (114, 1, 53);
INSERT INTO `sys_role_menu` VALUES (115, 1, 54);
INSERT INTO `sys_role_menu` VALUES (116, 1, 55);
INSERT INTO `sys_role_menu` VALUES (117, 1, 56);
INSERT INTO `sys_role_menu` VALUES (118, 1, 57);
INSERT INTO `sys_role_menu` VALUES (119, 1, 58);
INSERT INTO `sys_role_menu` VALUES (120, 1, 59);
INSERT INTO `sys_role_menu` VALUES (121, 1, 60);
INSERT INTO `sys_role_menu` VALUES (122, 1, 61);
INSERT INTO `sys_role_menu` VALUES (123, 2, 33);
INSERT INTO `sys_role_menu` VALUES (124, 2, 37);
INSERT INTO `sys_role_menu` VALUES (125, 2, 12);
INSERT INTO `sys_role_menu` VALUES (126, 2, 14);
INSERT INTO `sys_role_menu` VALUES (127, 2, 47);
INSERT INTO `sys_role_menu` VALUES (128, 2, 52);
INSERT INTO `sys_role_menu` VALUES (129, 2, 54);
INSERT INTO `sys_role_menu` VALUES (130, 2, 27);
INSERT INTO `sys_role_menu` VALUES (131, 2, 60);
INSERT INTO `sys_role_menu` VALUES (132, 2, 43);
INSERT INTO `sys_role_menu` VALUES (154, 1, 85);
INSERT INTO `sys_role_menu` VALUES (155, 1, 86);
INSERT INTO `sys_role_menu` VALUES (156, 1, 87);
INSERT INTO `sys_role_menu` VALUES (1867209863487127555, 1, 1867209453162561538);
INSERT INTO `sys_role_menu` VALUES (1867209863487127556, 1, 87);
INSERT INTO `sys_role_menu` VALUES (1867209863487127557, 1, 88);


-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1,'o1', 'superAdmin', 'a66abb5684c45962d887564f08346e8d', '', '', '奥特曼', '/logo.svg', '我是超级管理员', 0, '2001-02-21 16:00:00', 0, '2024-05-09 17:22:35', 1, '2024-09-11 11:23:20', 1, '内网IP', 0);
INSERT INTO `sys_user` VALUES (2,'o2', 'admin', 'a66abb5684c45962d887564f08346e8d', '', '', '哆啦A梦', '/logo.svg', '', 0, '2024-08-02 16:00:00', 0, '2024-07-10 22:20:18', 1, '2024-08-27 17:49:29', 1, NULL, 0);
INSERT INTO `sys_user` VALUES (3,'o3', 'user', 'a66abb5684c45962d887564f08346e8d', '', '', '哆啦A梦', '/logo.svg', '', 0, '2024-08-02 16:00:00', 0, '2024-07-10 22:20:18', 1, '2024-08-27 17:49:29', 1, NULL, 0);

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 1, 2);
INSERT INTO `sys_user_role` VALUES (3, 2, 2);
INSERT INTO `sys_user_role` VALUES (4, 3, 3);


-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (9, 'oper_business_type', '操作日志业务类型', 0, 0, 1, '2024-09-14 09:49:13', 1, '2024-09-14 09:49:19', '操作日志业务类型');
INSERT INTO `sys_dict` VALUES (10, 'oper_operator_type', '操作日志操作人类型', 0, 0, 1, '2024-09-20 10:16:50', 1, '2024-09-14 09:49:19', '操作日志业务类型');

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (17, 9, '其他', '0', 1, '', 0, 1, '2024-09-14 14:39:31', NULL, NULL, 'OTHER');
INSERT INTO `sys_dict_data` VALUES (18, 9, '新增', '1', 1, '', 0, 1, '2024-09-14 14:39:51', NULL, NULL, 'INSERT');
INSERT INTO `sys_dict_data` VALUES (19, 9, '修改', '2', 1, '', 0, 1, '2024-09-14 14:41:27', NULL, NULL, 'UPDATE');
INSERT INTO `sys_dict_data` VALUES (20, 9, '删除', '3', 1, '', 0, 1, '2024-09-14 14:41:48', NULL, NULL, 'DELETE');
INSERT INTO `sys_dict_data` VALUES (21, 9, '授权', '4', 1, '', 0, 1, '2024-09-14 14:42:37', NULL, NULL, 'GRANT');
INSERT INTO `sys_dict_data` VALUES (22, 9, '导出', '5', 1, '', 0, 1, '2024-09-14 14:43:12', NULL, NULL, 'EXPORT');
INSERT INTO `sys_dict_data` VALUES (23, 9, '导入', '6', 1, '', 0, 1, '2024-09-14 14:44:06', NULL, NULL, 'IMPORT');
INSERT INTO `sys_dict_data` VALUES (24, 9, '强退', '7', 1, '', 0, 1, '2024-09-14 14:44:30', NULL, NULL, 'FORCE');
INSERT INTO `sys_dict_data` VALUES (25, 9, '清空', '8', 1, '', 0, 1, '2024-09-14 14:44:53', NULL, NULL, 'CLEAR');
INSERT INTO `sys_dict_data` VALUES (26, 10, '后台用户', '1', 1, '', 0, 1, '2024-09-20 10:17:37', NULL, NULL, '');
INSERT INTO `sys_dict_data` VALUES (27, 10, '其他', '0', 1, '', 0, 1, '2024-09-20 14:35:30', NULL, NULL, '');

