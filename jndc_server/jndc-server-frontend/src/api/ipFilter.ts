import request from '../utils/request';
import { IpRule, IpRecord, PaginationParams, PageListVO, ClearIpRecordParams } from '../types';

export const ipFilterApi = {
  // 获取黑名单
  getBlackList: (params: PaginationParams) => {
    return request.post<any, PageListVO<IpRule>>('/blackList', params);
  },

  // 获取白名单
  getWhiteList: (params: PaginationParams) => {
    return request.post<any, PageListVO<IpRule>>('/whiteList', params);
  },

  // 添加到黑名单
  addToBlackList: (ip: string) => {
    return request.post<any, void>('/addToIpBlackList', { ip });
  },

  // 添加到白名单
  addToWhiteList: (ip: string) => {
    return request.post<any, void>('/addToIpWhiteList', { ip });
  },

  // 删除 IP 规则
  deleteIpRule: (id: number) => {
    return request.post<any, void>('/deleteIpRuleByPrimaryKey', { id });
  },

  // 获取被阻止记录
  getBlockRecord: (params: PaginationParams) => {
    return request.post<any, PageListVO<IpRecord>>('/blockRecord', params);
  },

  // 获取被允许记录
  getReleaseRecord: (params: PaginationParams) => {
    return request.post<any, PageListVO<IpRecord>>('/releaseRecord', params);
  },

  // 清空 IP 记录
  clearIpRecord: (params: ClearIpRecordParams) => {
    return request.post<any, void>('/clearIpRecord', params);
  },

  // 获取当前设备 IP
  getCurrentDeviceIp: () => {
    return request.post<any, string>('/getCurrentDeviceIp');
  },
};
