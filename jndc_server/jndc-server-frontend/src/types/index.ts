// API 响应通用格式
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
}

// 分页请求（对应后端 PageDTO：page + rows）
export interface PaginationParams {
  page: number;
  rows: number;
}

// 分页响应（对应后端 PageListVO：{ page, rows, total, data }）
export interface PaginationResult<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

// 后端 PageListVO 实际返回结构
export interface PageListVO<T> {
  page: number;
  rows: number;
  total: number;
  data: T[];
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
  lastHeartbeat: number;
  connected: boolean;
  online: boolean;
  authMode: number;
  lastSeenAt: number;
  lastOfflineAt: number;
  osName: string;
  osVersion: string;
  cpuModel: string;
  cpuLogicalCores: number;
  gpuNames: string[];
  memoryTotalBytes: number;
  diskTotalBytes: number;
  diskFreeBytes: number;
  clientToServerBytes: number;
  serverToClientBytes: number;
  clientToServerBandwidth: number;
  serverToClientBandwidth: number;
  trafficUpdatedAt: number;
}

export type TrafficTrendRange = '1hour' | '24hour' | '7day' | '1month' | '1year';

export interface ChannelTrafficTrendPoint {
  timestamp: number;
  clientToServerBytes: number;
  serverToClientBytes: number;
  totalBytes: number;
}

export interface ChannelTrafficTrendResult {
  range: TrafficTrendRange;
  bucketUnit: 'minute' | 'hour' | 'day' | 'month';
  points: ChannelTrafficTrendPoint[];
}

export interface ChannelRecord {
  id: string;
  channelId: string;
  clientId: string;
  ip: string;
  port: number;
  timeStamp: number;
  disconnectReason: string;
}

// 服务相关
export interface ServiceDescription {
  id?: string;
  serviceName: string;
  serviceIp: string;
  servicePort: number;
  clientId?: string;
  clientIp?: string;
  description?: string;
}

export interface ControlledServiceState {
  clientId: string;
  online: boolean;
  authMode: number;
  targetServices: ServiceDescription[];
  actualServices: ServiceDescription[];
}

// 端口绑定相关
export interface ServerPortBind {
  id: string;
  port: number;
  routeTo?: string | null;
  status: number; // 0: 未绑定, 1: 已绑定, 2: 绑定中
  portEnable?: number;
  enableDateRange?: string | null;
  timeRangeStart?: string;
  timeRangeEnd?: string;
  createTime?: string;
}

export interface CreatePortParams {
  port: number;
  timeRangeStart?: string;
  timeRangeEnd?: string;
}

export interface ServiceBindParams {
  id: string;
  serviceId?: string;
  routeTo?: string;
}

export interface DateRangeEditParams {
  id: string;
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
