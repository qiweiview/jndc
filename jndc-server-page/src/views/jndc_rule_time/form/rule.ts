import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  belongId: [{ required: true, message: "所属id为必填", trigger: "blur" }],
  effectEndTime: [
    { required: true, message: "截至时间为必填", trigger: "blur" }
  ],
  effectStartTime: [
    { required: true, message: "起始时间为必填", trigger: "blur" }
  ],
  ruleName: [{ required: true, message: "规则名称为必填", trigger: "blur" }],
  ruleStatus: [{ required: true, message: "是否生效为必填", trigger: "blur" }],
  ruleType: [{ required: true, message: "规则类型为必填", trigger: "blur" }]
});
