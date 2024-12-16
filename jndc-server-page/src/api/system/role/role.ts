import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysRole";
/** 新增角色 */
export const addRole = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/create`), { data });
};
/** 角色列表 */
export const listRole = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), { params });
};

/** 修改角色 */
export const updateRole = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/update`), { data });
};

/** 删除角色 */
export const deleteRole = (id: string) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), {
    data: { id: id }
  });
};

/** 获取所有角色 */
export const listAllSimpleRole = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/listAll`), { params });
};
