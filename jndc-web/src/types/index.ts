// API 响应通用格式
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
}

// 分页请求
export interface PaginationParams {
  page: number;
  pageSize: number;
}

// 分页响应
export interface PaginationResult<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

// 登录相关
export interface LoginParams {
  name: string;
  passWord: string;
}

export interface LoginResult {
  token: string;
}

// 隧道/通道相关
export interface ChannelContext {
  channelId: string;
  clientId: string;
  clientIp: string;
  clientPort: number;
  serviceCount: number;
  lastHeartbeat: string;
  connected: boolean;
}

export interface ChannelRecord {
  id: number;
  channelId: string;
  clientId: string;
  clientIp: string;
  disconnectTime: string;
  reason: string;
}

// 服务相关
export interface ServiceDescription {
  serviceName: string;
  serviceIp: string;
  servicePort: number;
  clientId: string;
  clientIp: string;
}

// 端口绑定相关
export interface ServerPortBind {
  id: number;
  port: number;
  routeTo: string;
  status: number; // 0: 未绑定, 1: 已绑定
  timeRangeStart: string;
  timeRangeEnd: string;
  createTime: string;
}

export interface CreatePortParams {
  port: number;
  timeRangeStart?: string;
  timeRangeEnd?: string;
}

export interface ServiceBindParams {
  id: number;
  routeTo: string;
}

export interface DateRangeEditParams {
  id: number;
  timeRangeStart: string;
  timeRangeEnd: string;
}

// IP 过滤相关
export interface IpRule {
  id: number;
  ip: string;
  type: number; // 0: 白名单, 1: 黑名单
  createTime: string;
}

export interface IpRecord {
  id: number;
  ip: string;
  accessCount: number;
  lastAccessTime: string;
}

export interface ClearIpRecordParams {
  date?: string;
  keepTop10?: boolean;
}

// HTTP 应用/域名路由相关
export interface HostRouteRule {
  id: number;
  hostKeyword: string;
  routeType: number; // 1: 转发, 2: 重定向, 3: 固定响应
  routeTarget: string;
  responseContent: string;
  responseCode: number;
  createTime: string;
}

export interface SaveHostRouteParams {
  hostKeyword: string;
  routeType: number;
  routeTarget?: string;
  responseContent?: string;
  responseCode?: number;
}

export interface UpdateHostRouteParams extends SaveHostRouteParams {
  id: number;
}

// WebSocket 消息
export interface WsMessage {
  type: number;
  data: string;
}
