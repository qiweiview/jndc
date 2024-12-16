import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysUser";
/** 获取用户列表 */
export const getUserList = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), { params });
};
/** 新增用户 */
export const addUser = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/create`), { data });
};
/** 修改基本用户 */
export const updateUser = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/update`), { data });
};
/** 修改用户状态 */
export const updateUserStutus = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/updateStatus`), { data });
};
/** 删除用户 */
export const deleteUser = (data: object) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), { data });
};
/** 重置密码 */
export const resetPassword = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/resetPassword`), {
    data
  });
};
// 获取用户详情
export const getUserDetail = (id: string) => {
  return http.request<any>("post", baseUrlApi(`${path}/getDetail`), {
    data: { idString: id }
  });
};
