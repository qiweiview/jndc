import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysDict";
/** 新增字典 */
export const addDict = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/create`), { data });
};
/** 字典列表 */
export const listDict = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), { params });
};

/** 修改字典 */
export const updateDict = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/update`), { data });
};

/** 删除字典 */
export const deleteDict = (id: number) => {
  return http.request<any>("delete", baseUrlApi(`/sysDict/delete`), {
    data: { id: id }
  });
};
// 获取全部字典以及字典数据
export const getAllDictAndData = () => {
  return http.request<any>("get", baseUrlApi(`${path}/getAllDictAndData`));
};
