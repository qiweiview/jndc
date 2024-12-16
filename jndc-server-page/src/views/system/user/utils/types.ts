// 虽然字段很少 但是抽离出来 后续有扩展字段需求就很方便了

interface FormItemProps {
  id?: number;
  /** 用户id */
  idString: string;
  /** 用户名（登录名） */
  username: string;
  /** 密码 */
  password: string;
  /** 昵称 */
  nickname: string;
  /** 邮箱 */
  email: string;
  /** 手机号 */
  phone: string;
  /** 性别 */
  gender: number;
  /** 头像 */
  avatar: string;
  /** 生日 */
  birthday: Date;
  /** 状态 */
  status: number;
  /** 简介 */
  intro: string;
}

interface FormProps {
  formInline: FormItemProps;
}

// 分配角色
interface RoleFormItemProps {
  userId: number;
  username: string;
  /** 选中的角色列表 */
  roleIds: Record<number, unknown>[];
  allRoles: { id: number; roleName: string }[];
}
interface RoleFormProps {
  formInline: RoleFormItemProps;
}
export type { FormItemProps, FormProps, RoleFormItemProps, RoleFormProps };
