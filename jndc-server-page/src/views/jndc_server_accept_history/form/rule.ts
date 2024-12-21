import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  clientId: [{ required: true, message: "客户端id为必填", trigger: "blur" }],
  connectTime: [{ required: true, message: "连接时间为必填", trigger: "blur" }],
  interruptTime: [
    { required: true, message: "中断时间为必填", trigger: "blur" }
  ],
  serverId: [{ required: true, message: "服务id为必填", trigger: "blur" }],
  sourceIp: [{ required: true, message: "来源ip为必填", trigger: "blur" }],
  sourcePort: [{ required: true, message: "来源端口为必填", trigger: "blur" }]
});
