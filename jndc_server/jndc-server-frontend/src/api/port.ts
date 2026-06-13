import request from '../utils/request';
import {
  ServerPortBind,
  CreatePortParams,
  ServiceBindParams,
  DateRangeEditParams,
} from '../types';

type BackendServerPortBind = {
  id: string;
  port: number;
  routeTo?: string | null;
  portEnable?: number;
  enableDateRange?: string | null;
  createTime?: string;
};

function normalizeServerPortBind(port: BackendServerPortBind): ServerPortBind {
  const enableDateRange = port.enableDateRange?.trim();
  const separator = enableDateRange?.includes(',')
    ? ','
    : enableDateRange?.includes('-')
      ? '-'
      : undefined;
  const [timeRangeStart, timeRangeEnd] =
    enableDateRange && separator
      ? enableDateRange.split(separator, 2).map((item) => item.trim())
      : [undefined, undefined];

  return {
    id: port.id,
    port: port.port,
    routeTo: port.routeTo,
    status: port.portEnable ?? 0,
    portEnable: port.portEnable ?? 0,
    enableDateRange: port.enableDateRange,
    timeRangeStart,
    timeRangeEnd,
    createTime: port.createTime,
  };
}

export const portApi = {
  // 获取端口列表
  getServerPortList: (port?: number) => {
    return request
      .post<any, BackendServerPortBind[]>('/getServerPortList', { port })
      .then((data) => data.map(normalizeServerPortBind));
  },

  // 创建端口监听
  createPortMonitoring: (params: CreatePortParams) => {
    return request.post<any, void>('/createPortMonitoring', params);
  },

  // 绑定服务
  doServiceBind: (params: ServiceBindParams) => {
    return request.post<any, void>('/doServiceBind', params);
  },

  // 编辑时间范围
  doDateRangeEdit: (params: DateRangeEditParams) => {
    return request.post<any, void>('/doDateRangeEdit', params);
  },

  // 停止服务绑定
  stopServiceBind: (id: string) => {
    return request.post<any, void>('/stopServiceBind', { id });
  },

  // 删除端口绑定记录
  deleteServiceBindRecord: (id: string) => {
    return request.post<any, void>('/deleteServiceBindRecord', { id });
  },

  // 重置绑定记录
  resetBindRecord: (id: string) => {
    return request.post<any, void>('/resetBindRecord', { id });
  },
};
