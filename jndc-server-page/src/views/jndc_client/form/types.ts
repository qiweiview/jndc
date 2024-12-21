// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  idString?: string;
  clientName?: string;
  clientRemark?: string;
  clientStatus?: string;
  createTime?: string;
  disguisedProtocol?: string;
  serverHost?: string;
  serverPort?: string;
  uniqueId?: string;
  updateTime?: string;
}

interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
