// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  idString?: string;
  bindHost?: string;
  bindPort?: number;
  bindType?: string;
  bindStatus?: string;
  createTime?: string;
  serverId?: string;
  sourceClientId?: string;
  sourceServiceId?: string;
  metaData?: string;
}

interface FormProps {
  formInline: FormItemProps;
}

interface MockMetaData {
  contentType: string;
  mockData: string;
  useSSL: boolean;
}

export type { FormItemProps, FormProps, MockMetaData };
