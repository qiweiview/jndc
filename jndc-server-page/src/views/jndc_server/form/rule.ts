import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  bindHost: [
    { required: true, message: "监听域名为必填", trigger: "blur" },
    {
      pattern:
        /^(https?:\/\/)?([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}|(\d{1,3}\.){3}\d{1,3}$/,
      message: "请输入正确的域名或IP",
      trigger: "blur"
    }
  ],
  bindPort: [{ required: true, message: "监听端口为必填", trigger: "blur" }],
  serverName: [{ required: true, message: "服务名称为必填", trigger: "blur" }],
  serverStatus: [{ required: true, message: "服务状态为必填", trigger: "blur" }]
});
