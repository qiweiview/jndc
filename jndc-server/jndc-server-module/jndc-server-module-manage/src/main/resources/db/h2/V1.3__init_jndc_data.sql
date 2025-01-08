INSERT INTO `sys_menu`
VALUES (1867209453162561537, 'JNDCManage', '服务端', 0, 1, '/server', '', NULL, 0, 0, 0, '', 'ri:server-fill',
        1, '2024-12-12 22:06:39', 1, '2024-12-12 22:15:34', '', 0, '', 0, '');


INSERT INTO `sys_menu`
VALUES (1867209707094114306, 'JNDCServer', '服务列表', 1867209453162561537, 1, '/server/list', 'jndc_server/index',
        NULL, 0, 1, 0, '', 'ep:brush-filled', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');

INSERT INTO `sys_menu`
VALUES (1867209707094114311, 'RegisteredService', '在册服务', 1867209453162561537, 2, '/server/jndc_client_service', 'jndc_client_service/index',
        NULL, 0, 1, 0, '', 'ep:service', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');



INSERT INTO `sys_menu`
VALUES (1867209707094114308, 'JNDCAccessHistory', '访问记录', 1867209453162561537, 3, '/server/jndc_access_history',
        'jndc_access_history/index',
        NULL, 0, 1, 0, '', 'fa-solid:eye', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');

INSERT INTO `sys_menu`
VALUES (1867209707094114309, 'JNDCServerApp', '服务应用', 1867209453162561537, 4, '/server/jndc_server_app',
        'jndc_server_app/index',
        NULL, 0, 1, 0, '', 'ri:app-store-fill', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');


INSERT INTO `sys_menu`
VALUES (1867209707094114310, 'JNDCManage', '客户端', 0, 1, '/client', '', NULL, 0, 0, 0, '', 'ri:shapes-fill',
        1, '2024-12-12 22:06:39', 1, '2024-12-12 22:15:34', '', 0, '', 0, '');


INSERT INTO `sys_menu`
VALUES (1867209707094114307, 'JNDCClient', '列表', 1867209707094114310, 2, '/client/jndc_client', 'jndc_client/index',
        NULL, 0, 1, 0, '', 'ep:brush-filled', 1, '2024-12-12 22:07:40', 1, '2024-12-12 22:15:38', '', 0, '', 0, '');



INSERT INTO `sys_role_menu`
VALUES (1867209863482933250, 1, 1867209453162561537);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127558, 1, 1867209707094114306);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127559, 1, 1867209707094114307);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127560, 1, 1867209707094114308);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127561, 1, 1867209707094114309);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127562, 1, 1867209707094114310);

INSERT INTO `sys_role_menu`
VALUES (1867209863487127563, 1, 1867209707094114311);

