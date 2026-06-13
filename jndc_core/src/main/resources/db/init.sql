CREATE TABLE IF NOT EXISTS "channel_context_record"
(
    "id"                text(128) NOT NULL,
    "client_id"         text(255),
    "ip"                text(255),
    "channel_id"        text(255),
    "port"              integer,
    "time_stamp"        bigint,
    "disconnect_reason" text(255),
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "http_host_route"
(
    "id"                 text(128) NOT NULL,
    "route_type"         integer,
    "host_key_word"      text(255),
    "fixed_response"     text(2000),
    "redirect_address"   text(255),
    "fixed_content_type" text(255),
    "forward_host"       text(255),
    "forward_protocol"   text(255),
    "forward_port"       integer,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "ip_filter_record"
(
    "id"          text(32) NOT NULL,
    "ip"          text(255),
    "v_count"     integer,
    "time_stamp"  bigint,
    "record_type" integer,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "server_ip_filter_rule"
(
    "id"   text(32) NOT NULL,
    "ip"   text(16),
    "type" integer,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "server_port_bind"
(
    "id"                text(128) NOT NULL,
    "name"              text(255),
    "bind_client_id"    text(255),
    "enable_date_range" text(255),
    "port"              integer,
    "port_enable"       integer,
    "route_to"          text(255),
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "client_auth_record"
(
    "client_id"          text(255) NOT NULL,
    "client_auth_key"    text(255) NOT NULL,
    "auth_mode"          integer,
    "last_client_ip"     text(255),
    "last_client_port"   integer,
    "last_seen_at"       bigint,
    "last_offline_at"    bigint,
    "os_name"            text(255),
    "os_version"         text(255),
    "cpu_model"          text(255),
    "cpu_logical_cores"  integer,
    "gpu_names"          text(2000),
    "memory_total_bytes" bigint,
    "disk_total_bytes"   bigint,
    "disk_free_bytes"    bigint,
    "client_to_server_bytes" bigint,
    "server_to_client_bytes" bigint,
    PRIMARY KEY ("client_id")
);

CREATE TABLE IF NOT EXISTS "client_controlled_service"
(
    "id"           text(128) NOT NULL,
    "client_id"    text(255) NOT NULL,
    "service_name" text(255),
    "service_ip"   text(255) NOT NULL,
    "service_port" integer NOT NULL,
    "description"  text(255),
    PRIMARY KEY ("id")
);
