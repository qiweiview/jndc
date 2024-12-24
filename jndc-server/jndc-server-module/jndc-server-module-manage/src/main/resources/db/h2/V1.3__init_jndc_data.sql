INSERT INTO `sys_menu`
VALUES (1867209453162561537, 'JNDCManage', 'JNDC管理', 0, 1, '/jndc_namage', '', NULL, 0, 0, 0, '', 'ep:camera-filled',
        1, '2024-12-12 22:06:39', 1, '2024-12-12 22:15:34', '', 0, '', 0, '');

INSERT INTO `sys_menu`
VALUES (1867209707094114306, 'JNDCServer', '服务端', 1867209453162561537, 1, '/jndc/jndc_server', 'jndc_server/index',
        NULL, 0, 1, 0, '', 'fa-solid:server', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');

INSERT INTO `sys_menu`
VALUES (1867209707094114307, 'JNDCClient', '客户端', 1867209453162561537, 1, '/jndc/jndc_client', 'jndc_client/index',
        NULL, 0, 1, 0, '', 'ep:brush', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');

INSERT INTO `sys_menu`
VALUES (1867209707094114308, 'JNDCAccessHistory', '访问记录', 1867209453162561537, 1, '/jndc/jndc_access_history',
        'jndc_access_history/index',
        NULL, 0, 1, 0, '', 'fa-solid:eye', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');



INSERT INTO `sys_role_menu`
VALUES (1867209863482933250, 1, 1867209453162561537);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127558, 1, 1867209707094114306);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127559, 1, 1867209707094114307);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127560, 1, 1867209707094114308);

