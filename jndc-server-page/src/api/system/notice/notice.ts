import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysNotice";
/** 获取通知列表 */
export const getNoticeList = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), { params });
};
/** 新增通知公告 */
export const addNotice = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/create`), { data });
};
/** 修改基本通知公告 */
export const updateNotice = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/update`), { data });
};
/** 删除通知公告 */
export const deleteNotice = (data: object) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), { data });
};
// 获取通知公告详情
export const getNoticeDetail = (id: number) => {
  return http.request<any>("post", baseUrlApi(`${path}/getDetail`), {
    data: { id: id }
  });
};
/** 获取用户的通知列表 */
export const getNoticeListByUser = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/listByUser`), { params });
};

/** 修改基本通知公告 */
export const setRead = (id: number) => {
  return http.request<any>("post", baseUrlApi(`${path}/setRead`), {
    data: { id: id }
  });
};
