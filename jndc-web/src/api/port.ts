import request from '../utils/request';
import {
  ServerPortBind,
  CreatePortParams,
  ServiceBindParams,
  DateRangeEditParams,
} from '../types';

export const portApi = {
  // 获取端口列表
  getServerPortList: (port?: number) => {
    return request.post<any, ServerPortBind[]>('/getServerPortList', { port });
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
  stopServiceBind: (id: number) => {
    return request.post<any, void>('/stopServiceBind', { id });
  },

  // 删除端口绑定记录
  deleteServiceBindRecord: (id: number) => {
    return request.post<any, void>('/deleteServiceBindRecord', { id });
  },

  // 重置绑定记录
  resetBindRecord: (id: number) => {
    return request.post<any, void>('/resetBindRecord', { id });
  },
};
