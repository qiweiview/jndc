import { http } from "@/utils/http";

export type UserResult = {
  /** 用户名 */
  username: string;
  /** 密码 */
  password: string;
  /** 当前登录用户的角色 */
  roles?: Array<string>;
  /** 按钮级别权限 */
  permissions?: Array<string>;
  /** `token` */
  accessToken?: string;
  /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
  expires?: Date;
};


/** 登录 */
export const getLogin = (data?: UserResult) => {
  return http.request<UserResult>("post", "/admin/login", { data });
};

