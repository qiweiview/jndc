import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 角色校验 */
export const formRules = reactive(<FormRules>{
  roleName: [{ required: true, message: "角色名称为必填项", trigger: "blur" }],
  roleCode: [{ required: true, message: "角色编码为必填项", trigger: "blur" }],
  status: [{ required: true, message: "状态为必填项", trigger: "blur" }]
});
