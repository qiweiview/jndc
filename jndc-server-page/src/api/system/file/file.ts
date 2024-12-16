import { http } from "@/utils/http";
import { baseUrlApi } from "@/api/utils";

const path = "/sysFile";
/** 模式一的文件上传，后端上传 */
export const upload = (data?: object) => {
  return http.request<any>(
    "post",
    baseUrlApi(`${path}/upload`),
    { data },
    {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    }
  );
};
/** 文件列表 */
export const listFile = (params?: object) => {
  return http.request<any>("get", baseUrlApi(`${path}/list`), {
    params
  });
};

/** 删除文件 */
export const deleteFile = (id: number) => {
  return http.request<any>("delete", baseUrlApi(`${path}/delete`), {
    data: { id: id }
  });
};

/** 模式二的文件上传，前端直接上传云存储 */
export const getFilePreSignedUrl = (path: string) => {
  return http.request<any>("get", baseUrlApi("/sysFile/getPreSignedUrl"), {
    params: {
      path
    }
  });
};
export const createFile = (data?: object) => {
  return http.request<any>("post", baseUrlApi(`${path}/create`), { data });
};
