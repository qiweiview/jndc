// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  fileList: any;
}
interface FormProps {
  formInline: FormItemProps;
}

export type { FormItemProps, FormProps };
