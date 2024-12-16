import { http } from "@/utils/http";
import { baseUrlApi } from "./utils";

export const getAsyncRoutes = (data?: object) => {
  return http.request<any>("get", baseUrlApi("/sysMenu/roleMenuTree"), {
    data
  });
};
