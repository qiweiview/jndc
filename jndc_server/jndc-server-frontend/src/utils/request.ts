import axios from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth-token');
    if (token && token !== '403') {
      config.headers['auth-token'] = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response;
    // 后端有两种返回格式：
    // 1. 动作类接口返回 ResponseMessage: { code: 0, message: "操作成功", data: ... }
    // 2. 列表类接口直接返回 PageListVO 或其他对象
    if (data && typeof data === 'object' && 'code' in data) {
      // ResponseMessage 格式
      if (data.code === 0) {
        return data.data;
      }
      message.error(data.message || '请求失败');
      return Promise.reject(new Error(data.message || '请求失败'));
    }
    // 非 ResponseMessage 格式，直接返回数据
    return data;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth-token');
      window.location.href = '/login';
      message.error('登录已过期，请重新登录');
    } else if (error.response?.status === 403) {
      localStorage.removeItem('auth-token');
      window.location.href = '/login';
      message.error('登录凭证无效，请重新登录');
    } else {
      message.error(error.message || '网络错误');
    }
    return Promise.reject(error);
  }
);

export default request;
