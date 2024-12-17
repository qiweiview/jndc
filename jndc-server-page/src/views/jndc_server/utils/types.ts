// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
}

interface FormProps {
  formInline: FormItemProps;
}

// 数据项
interface DictDataFormItemProps {
  id?: number;
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
