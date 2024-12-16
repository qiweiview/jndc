import axios from "axios";

export const baseUrlApi = (url: string) => `/admin${url}`;

export const GETNOBASE = async (url: string, params?: any): Promise<any> => {
  try {
    const data = await axios.get(url, {
      params: params
    });
    return data.data;
  } catch (error) {
    return Promise.reject(error);
  }
};
