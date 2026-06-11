import request from '../utils/request';
import { ServiceDescription } from '../types';

export const serviceApi = {
  // 获取服务列表
  getServiceList: (clientId?: string) => {
    return request.post<any, ServiceDescription[]>('/getServiceList', { clientId });
  },
};
