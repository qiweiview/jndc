import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  destination: [{ required: true, message: "访问目标为必填", trigger: "blur" }],
  destinationId: [{ required: true, message: "目标id为必填", trigger: "blur" }],
  packageSampling: [
    { required: true, message: "数据采样为必填", trigger: "blur" }
  ],
  remoteIp: [{ required: true, message: "ip地址为必填", trigger: "blur" }],
  remotePort: [{ required: true, message: "端口为必填", trigger: "blur" }]
});
