import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

/** 登录 */
export const getLogin = (data?: object) => {
  return http.request<any>("post", baseUrlApi("/login"), { data });
};
/** 注销 */
export const logOut = (data?: object) => {
  return http.request<any>("get", baseUrlApi("/logout"), { data });
};

/** 刷新token */
export const refreshTokenApi = (data?: object) => {
  return http.request<any>("post", "/refreshToken", { data });
};
// 退出登录
export const getLogout = () => {
  return http.request<any>("post", baseUrlApi("/logout"));
};
