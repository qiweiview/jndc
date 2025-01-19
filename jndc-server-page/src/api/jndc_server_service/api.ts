import { http } from "@/utils/http";

const path = "/jndcServerService";
/** 新增 */
export const addOperation = (data?: object) => {
  return http.request<any>("post", `${path}/save`, { data });
};

/** 列表 */
export const listOperation = (data?: object) => {
  return http.request<any>("post", `${path}/queryPage`, { data });
};

/** 修改 */
export const updateOperation = (data?: object) => {
  return http.request<any>("post", `${path}/update`, { data });
};

/** 删除 */
export const deleteOperation = (id: string) => {
  return http.request<any>("post", `${path}/delete`, {
    data: { idString: id }
  });
};

/** 删除批量 */
export const deleteBatchOperation = (id: string[]) => {
  return http.request<any>("post", `${path}/deleteBatch`, {
    data: { idString: id }
  });
};
