import request from '../utils/request';
import { ChannelContext, ChannelRecord, PaginationParams, PaginationResult } from '../types';

export const channelApi = {
  // 获取活跃隧道列表
  getServerChannelTable: () => {
    return request.post<any, ChannelContext[]>('/getServerChannelTable');
  },

  // 获取隧道断开记录
  getChannelRecord: (params: PaginationParams) => {
    return request.post<any, PaginationResult<ChannelRecord>>('/getChannelRecord', params);
  },

  // 清空隧道断开记录
  clearChannelRecord: () => {
    return request.post<any, void>('/clearChannelRecord');
  },

  // 发送心跳
  sendHeartBeat: (channelId: string) => {
    return request.post<any, void>('/sendHeartBeat', { channelId });
  },

  // 关闭隧道
  closeChannelByServer: (channelId: string) => {
    return request.post<any, void>('/closeChannelByServer', { channelId });
  },
};
