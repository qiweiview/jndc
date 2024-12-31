import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  bindHost: [{ required: true, message: "监听域名为必填", trigger: "blur" }],
  bindPort: [{ required: true, message: "监听端口为必填", trigger: "blur" }]
});
