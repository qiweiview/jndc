import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/account";
/** 获取用户角色 */

export const getAccountRole = () => {
  return http.request<any>("get", baseUrlApi(`${path}/getRole`));
};
// 修改个人信息
export const updateInfo = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/updateInfo`), {
    data
  });
};
// 发送更改密码验证码
export const sendPwdCode = () => {
  return http.request<any>("get", baseUrlApi(`${path}/sendPwdCode`));
};
// 发送更改邮箱验证码
export const sendEmailCode = (email?: string) => {
  return http.request<any>("post", baseUrlApi(`${path}/sendEmailCode`), {
    data: { email: email }
  });
};
// 修改密码
export const updatePassword = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/updatePassword`), {
    data
  });
};
// 修改邮箱
export const updateEmail = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/updateEmail`), {
    data
  });
};
