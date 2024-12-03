import { http } from "@/utils/http";
import type { Page } from "@/api/pageable";

export type PureUser = {
  idS?: string;
  /** 头像 */
  avatar?: string;
  /** 用户名 */
  username?: string;
  /** 密码 */
  password?: string;
  /** 昵称 */
  nickname?: string;
  /** 当前登录用户的角色 */
  roles?: string;
  /** 按钮级别权限 */
  permissions?: string;
  /** `token` */
  accessToken?: string;
  /** 用于调用刷新`accessToken`的接口时所需的`token` */
};

/** 刷新`token` */
export const queryUserPage = (data?: object) => {
  return http.request<Page<PureUser>>("post", "/admin/queryUserPage", {
    data
  });
};

export const createUser = (data: PureUser) => {
  return http.request("post", "/admin/createUser", { data });
};

export const deleteUser = (data: PureUser) => {
  return http.request("post", "/admin/deleteUser", { data });
};
export const resetPassword = (data: PureUser) => {
  return http.request("post", "/admin/resetPassword", { data });
};
