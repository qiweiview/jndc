import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  bindPort: [{ required: true, message: "绑定端口为必填", trigger: "blur" }],
  bindSource: [{ required: true, message: "绑定来源为必填", trigger: "blur" }],
  bindStatus: [{ required: true, message: "绑定状态为必填", trigger: "blur" }],
  latestBindResult: [{ required: true, message: "最后绑定结果为必填", trigger: "blur" }],
});
