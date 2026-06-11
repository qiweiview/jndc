CREATE TABLE IF NOT EXISTS "channel_context_record"
(
    "id"         text(128) NOT NULL,
    "ip"         text(255),
    "channel_id" text(255),
    "port"       integer,
    "time_stamp" bigint,
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
