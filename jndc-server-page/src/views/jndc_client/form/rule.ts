import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  bindServerHost: [
    { required: true, message: "服务主机为必填", trigger: "blur" }
  ],
  bindServerPort: [
    { required: true, message: "服务端口为必填", trigger: "blur" }
  ],
  clientName: [
    { required: true, message: "客户端名称为必填", trigger: "blur" }
  ],
  clientRemark: [
    { required: true, message: "客户端备注为必填", trigger: "blur" }
  ],
  clientStatus: [
    { required: true, message: "客户端状态为必填", trigger: "blur" }
  ],
  disguisedProtocol: [
    { required: true, message: "伪装协议为必填", trigger: "blur" }
  ]
});
