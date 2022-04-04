
CREATE TABLE IF NOT EXISTS `jndc`.`server_port_bind`  (
  `id` varchar(128) NOT NULL COMMENT '编号',
  `name` varchar(255) NULL COMMENT '名称',
  `bind_client_id` varchar(255) NULL COMMENT '客户端id',
  `enable_date_range` varchar(255) NULL COMMENT '可用时间范围',
  `port` int(11) NULL COMMENT '端口',
  `port_enable` int(1) NULL COMMENT '端口是否可用',
  `route_to` varchar(255) NULL COMMENT '路由链路',
  PRIMARY KEY (`id`)
) COMMENT = '端口绑定记录表';

CREATE TABLE IF NOT EXISTS `jndc`.`channel_context_record`  (
  `id` varchar(128) NOT NULL COMMENT '编号',
  `ip` varchar(255) NULL COMMENT 'ip地址',
  `channel_id` varchar(255) NULL COMMENT '隧道id',
  `port` int(8) NULL COMMENT '端口',
  `time_stamp` int(16) NULL COMMENT '时间戳',
  PRIMARY KEY (`id`)
) COMMENT = '隧道记录表';

CREATE TABLE IF NOT EXISTS `jndc`.`server_ip_filter_rule`  (
  `id` varchar(32) NOT NULL COMMENT '编号',
  `ip` varchar(16) NULL COMMENT 'ip地址',
  `type` int(1) NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) COMMENT = 'ip过滤规则表';

CREATE TABLE IF NOT EXISTS `jndc`.`http_host_route`  (
  `id` varchar(128) NOT NULL COMMENT '编号',
  `route_type` int NULL COMMENT '路由类型',
  `host_key_word` varchar(255) NULL COMMENT '路由域',
  `fixed_response` varchar(2000) NULL COMMENT '固定返回',
  `redirect_address` varchar(255) NULL COMMENT '重定向地址',
  `fixed_content_type` varchar(255) NULL COMMENT '固定返回类型content-type',
  `forward_host` varchar(255) NULL COMMENT '转发地址',
  `forward_protocol` varchar(255) NULL COMMENT '转发协议',
  `forward_port` int NULL COMMENT '转发端口',
  PRIMARY KEY (`id`)
) COMMENT = 'http域名配置表';

CREATE TABLE IF NOT EXISTS `jndc`.`ip_filter_record`  (
  `id` varchar(32) NOT NULL COMMENT '编号',
  `ip` varchar(255) NULL COMMENT 'ip地址',
  `v_count` int(32) NULL COMMENT '记录值',
  `time_stamp` int(16) NULL COMMENT '时间',
  `record_type` int(1) NULL COMMENT '记录类型',
  PRIMARY KEY (`id`)
) COMMENT = 'IP过滤记录表';