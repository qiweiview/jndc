// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  idString?: string;
  createTime?: string;
  updateTime?: string;
  clientId?: string;
  serviceName?: string;
  serviceHost?: string;
  servicePort?: string;
  expectPort?: string;
  serviceStatus?: string;
  serviceProtocol?: string;
  serviceMode?: string;
  serviceUniqueId?: string;
  serverId?: string;
}

interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
