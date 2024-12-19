import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  bindPort: [{ required: true, message: "监听端口为必填", trigger: "blur" }],
  serverName: [{ required: true, message: "服务名称为必填", trigger: "blur" }],
  serverStatus: [{ required: true, message: "服务状态为必填", trigger: "blur" }]
});
