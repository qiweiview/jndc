import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  dictName: [{ required: true, message: "字典名称为必填项", trigger: "blur" }],
  dictCode: [{ required: true, message: "字典编码为必填项", trigger: "blur" }],
  status: [{ required: true, message: "状态为必填项", trigger: "blur" }]
});
/** 字典数据校验 */
export const dictDataformRules = reactive(<FormRules>{
  name: [{ required: true, message: "数据项名称为必填项", trigger: "blur" }],
  value: [{ required: true, message: "数据项值为必填项", trigger: "blur" }],
  status: [{ required: true, message: "状态为必填项", trigger: "blur" }],
  sortOrder: [{ required: true, message: "排序为必填项", trigger: "blur" }]
});
