import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysConfig";
const platformConfigKey = "admin.platform.theme";
const footerMessageKey = "admin.footer.message";
/** 新增配置 */
export const addConfig = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/create`), { data });
};
/** 配置列表 */
export const listConfig = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), {
    params
  });
};
/** 配置详情 */
export const getConfig = (id: string) => {
  return http.request<any>("post", baseUrlApi(`${path}/get`), {
    data: { idString: id }
  });
};

/** 修改配置 */
export const updateConfig = (data?: object) => {
  return http.request<any>("put", baseUrlApi(`${path}/update`), {
    data
  });
};

/** 修改配置为主配置 */
export const updateConfigMaster = (id: string) => {
  return http.request<any>("put", baseUrlApi(`${path}/updateMaster/`), {
    data: { idString: id }
  });
};

/** 删除配置 */
export const deleteConfig = (id: string) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), {
    data: { idString: id }
  });
};

export const refreshCache = () => {
  return http.request<any>("get", baseUrlApi(`${path}/refreshCache`));
};

export const getPlatformConfig = () => {
  return http.request<any>("post", baseUrlApi(`${path}/getByKey`), {
    data: { configKey: platformConfigKey }
  });
};
// 页脚信息footerMessageKey
export const getFooterMessage = () => {
  return http.request<any>("post", baseUrlApi(`${path}/getByKey`), {
    data: { configKey: footerMessageKey }
  });
};
