import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  autoRegister: [
    { required: true, message: "是否自动注册为必填", trigger: "blur" }
  ],
  serviceHost: [{ required: true, message: "服务主机为必填", trigger: "blur" }],
  serviceName: [{ required: true, message: "服务名称为必填", trigger: "blur" }],
  servicePort: [{ required: true, message: "服务端口为必填", trigger: "blur" }],
  serviceStatus: [
    { required: true, message: "服务状态为必填", trigger: "blur" }
  ]
});
