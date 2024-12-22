// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  idString?: string;
  autoRegister?: number;
  clientId?: string;
  clientIdString?: string;
  createTime?: string;
  expectPort?: number;
  serviceHost?: string;
  serviceMode?: string;
  serviceName?: string;
  servicePort?: number;
  serviceProtocol?: string;
  serviceStatus?: string;
  serviceUniqueId?: string;
  updateTime?: string;
}

interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
