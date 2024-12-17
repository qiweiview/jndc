import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/jndcServer";
/** 新增 */
export const addOperation = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/save`), { data });
};
/** 列表 */
export const listOperation = (params?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/queryPage`), { params });
};

/** 修改 */
export const updateOperation = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/update`), { data });
};

/** 删除 */
export const deleteOperation = (id: string) => {
  return http.request<any>("post", baseUrlApi(`${path}/delete`), {
    data: { idString: id }
  });
};
/** 删除 */
export const deleteBatchOperation = (id: string[]) => {
  return http.request<any>("post", baseUrlApi(`${path}/delete`), {
    data: { idString: id }
  });
};
