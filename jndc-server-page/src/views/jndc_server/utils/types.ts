// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  idString?: string;
  serverName?: string;
}

interface FormProps {
  formInline: FormItemProps;
}

// 数据项
interface DictDataFormItemProps {
  id?: number;
  serverName?: string;
}

//
interface DictDataFormProps {
  formInline: DictDataFormItemProps;
}

export type {
  FormItemProps,
  FormProps,
  DictDataFormItemProps,
  DictDataFormProps
};
