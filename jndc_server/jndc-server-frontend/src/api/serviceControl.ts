import request from '../utils/request';
import { ControlledServiceState, ServiceDescription } from '../types';

export const serviceControlApi = {
  getClientControlledServiceList: (clientId: string) => {
    return request.post<any, ControlledServiceState>('/getClientControlledServiceList', { clientId });
  },

  replaceClientControlledServices: (clientId: string, services: ServiceDescription[]) => {
    return request.post<any, void>('/replaceClientControlledServices', { clientId, services });
  },
};
