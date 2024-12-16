import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysDictData";
/** 新增字典数据项 */
export const addDictData = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/create`), { data });
};
/** 字典数据项列表 */
export const listDictData = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), { params });
};

/** 修改字典数据项 */
export const updateDictData = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/update`), { data });
};

/** 删除字典数据项 */
export const deleteDictData = (data?: object) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), { data });
};
