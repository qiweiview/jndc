import request from '../utils/request';
import { ChannelContext, ChannelRecord } from '../types';

export const channelApi = {
  // 获取设备/隧道列表
  getServerChannelTable: () => {
    return request.post<any, ChannelContext[]>('/getServerChannelTable');
  },

  // 获取某个设备最近断开记录
  getRecentChannelRecordByClientId: (clientId: string) => {
    return request.post<any, ChannelRecord[]>('/getRecentChannelRecordByClientId', { clientId });
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
