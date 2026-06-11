import request from '../utils/request';
import { LoginParams, LoginResult } from '../types';

export const authApi = {
  login: (params: LoginParams) => {
    return request.post<any, LoginResult>('/login', params);
  },
};
