// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  /** 字典名称 */
  dictName: string;
  /** 字典编码 */
  dictCode: string;
  /** 状态 */
  status: number;
  /** 备注 */
  remark: string;
}
interface FormProps {
  formInline: FormItemProps;
}
// 数据项
interface DictDataFormItemProps {
  id?: number;
  /** 字典ID */
  dictId: number;
  /** 数据项名称 */
  name: string;
  /** 数据项值 */
  value: string;
  /** 颜色 */
  color: string;
  /** 状态 */
  status: number;
  /** 排序 */
  sortOrder: number;
  /** 备注 */
  remark: string;
}
interface DictDataFormProps {
  formInline: DictDataFormItemProps;
}
export type {
  FormItemProps,
  FormProps,
  DictDataFormItemProps,
  DictDataFormProps
};
