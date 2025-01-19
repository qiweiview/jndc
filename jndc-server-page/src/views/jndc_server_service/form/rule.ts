import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  clientId: [{ required: true, message: "所属客户端id为必填", trigger: "blur" }],
  serviceName: [{ required: true, message: "服务名称为必填", trigger: "blur" }],
  serviceHost: [{ required: true, message: "服务主机为必填", trigger: "blur" }],
  servicePort: [{ required: true, message: "服务端口为必填", trigger: "blur" }],
  expectPort: [{ required: true, message: "期望端口为必填", trigger: "blur" }],
  serviceStatus: [{ required: true, message: "服务状态为必填", trigger: "blur" }],
  serviceProtocol: [{ required: true, message: "服务协议为必填", trigger: "blur" }],
  serviceMode: [{ required: true, message: "服务模式为必填", trigger: "blur" }],
  serviceUniqueId: [{ required: true, message: "服务唯一id为必填", trigger: "blur" }],
  serverId: [{ required: true, message: "所属服务端id为必填", trigger: "blur" }],
});
