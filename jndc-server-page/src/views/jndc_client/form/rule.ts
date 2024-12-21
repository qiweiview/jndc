import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  clientName: [
    { required: true, message: "客户端名称为必填", trigger: "blur" }
  ],
  clientStatus: [
    { required: true, message: "客户端状态为必填", trigger: "blur" }
  ],
  serverHost: [{ required: true, message: "服务主机为必填", trigger: "blur" }],
  serverPort: [{ required: true, message: "服务端口为必填", trigger: "blur" }]
});
