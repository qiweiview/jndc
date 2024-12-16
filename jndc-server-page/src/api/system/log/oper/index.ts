import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysOperLog";

/** 操作日志列表 */
export const listOperLog = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), {
    params
  });
};

/** 删除操作日志 */
export const deleteOperLog = (data?: object) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), {
    data
  });
};

/** 清空操作日志 */
export const clearOperLog = () => {
  return http.request<any>("delete", baseUrlApi(`${path}/clear`));
};
