import { http } from "@/utils/http";

const path = "/mock/big-screen";

export const getMapData = (params?: object) => {
  return http.request<any>("get", `${path}/map-data`, { params });
};
