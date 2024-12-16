import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysLoginLog";

/** 登录日志列表 */
export const listLoginLog = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), {
    params
  });
};

/** 删除登录日志 */
export const deleteLoginLog = (data?: object) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), {
    data
  });
};

/** 清空登录日志 */
export const clearLoginLog = () => {
  return http.request<any>("delete", baseUrlApi(`${path}/clear`));
};
