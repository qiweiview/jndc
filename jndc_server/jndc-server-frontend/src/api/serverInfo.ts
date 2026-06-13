import request from '../utils/request';

export interface ServerRuntimeInfoVO {
  bindIp: string;
  servicePort: number;
  managementApiPort: number;
  httpPort: number;
  secrete: string;
}

export const getServerRuntimeInfo = () => {
  return request.post<any, ServerRuntimeInfoVO>('/getServerRuntimeInfo');
};
