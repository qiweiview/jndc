import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  bindHost: [{ required: true, message: "监听域名为必填", trigger: "blur" }],
  bindPort: [{ required: true, message: "监听端口为必填", trigger: "blur" }],
  bindStatus: [{ required: true, message: "监听状态为必填", trigger: "blur" }],
  serverId: [{ required: true, message: "jndc服务id为必填", trigger: "blur" }],
  sourceClientId: [{ required: true, message: "来源客户端为必填", trigger: "blur" }],
  sourceServiceId: [{ required: true, message: "来源服务为必填", trigger: "blur" }],
});
