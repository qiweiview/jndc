// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  title: string;
  content: string;
  status: number;
  type: number;
  remark?: string;
  roleIds?: number[];
  allRoles: { id: number; roleName: string }[];
  editorHeight?: string;
}
interface FormProps {
  formInline: FormItemProps;
}
export type { FormItemProps, FormProps };
