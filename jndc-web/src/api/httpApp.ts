import request from '../utils/request';
import {
  HostRouteRule,
  SaveHostRouteParams,
  UpdateHostRouteParams,
  PaginationParams,
  PageListVO,
} from '../types';

export const httpAppApi = {
  // 获取路由规则列表
  listHostRouteRule: (params: PaginationParams) => {
    return request.post<any, PageListVO<HostRouteRule>>('/listHostRouteRule', params);
  },

  // 保存路由规则
  saveHostRouteRule: (params: SaveHostRouteParams) => {
    return request.post<any, void>('/saveHostRouteRule', params);
  },

  // 更新路由规则
  updateHostRouteRule: (params: UpdateHostRouteParams) => {
    return request.post<any, void>('/updateHostRouteRule', params);
  },

  // 删除路由规则
  deleteHostRouteRule: (id: number) => {
    return request.post<any, void>('/deleteHostRouteRule', { id });
  },
};
