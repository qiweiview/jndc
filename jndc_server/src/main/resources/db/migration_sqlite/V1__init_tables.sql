CREATE TABLE "ip_filter_record"
(
    "id"          text(32) NOT NULL,
    "ip"          TEXT(16),
    "v_count"     integer(32),
    "time_stamp"  integer(64),
    "record_type" integer(2),
    PRIMARY KEY ("id")
);

CREATE TABLE "http_host_route"
(
    "id"                 text(32) NOT NULL,
    "route_type"         INTEGER(2),
    "host_key_word"      text(200),
    "fixed_response"     text(2000),
    "redirect_address"   text(500),
    "fixed_content_type" text(100),
    "forward_host"       text(100),
    "forward_protocol"   text(30),
    "forward_port"       INTEGER(20),
    PRIMARY KEY ("id")
);

CREATE TABLE "channel_context_record"
(
    "id"         text(32) NOT NULL,
    "ip"         text(16),
    "channel_id" text(32),
    "port"       integer(8),
    "time_stamp" integer(64),
    PRIMARY KEY ("id")
);

CREATE TABLE "server_ip_filter_rule"
(
    "id"   text(32) NOT NULL,
    "ip"   text(32),
    "type" integer(1),
    PRIMARY KEY ("id")
);

CREATE TABLE "server_port_bind"
(
    "id"                text(32) NOT NULL,
    "name"              text(50),
    "bind_client_id"    text(32),
    "enable_date_range" text(50),
    "port"              integer(10),
    "port_enable"       integer(2),
    "route_to"          text(16),
    PRIMARY KEY ("id")
);