import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 字典校验 */
export const formRules = reactive(<FormRules>{
  logContent: [{ required: true, message: "日志内容为必填", trigger: "blur" }],
  logTime: [{ required: true, message: "创建日期为必填", trigger: "blur" }],
  logType: [{ required: true, message: "日志类型为必填", trigger: "blur" }],
  sourceId: [{ required: true, message: "来源id为必填", trigger: "blur" }]
});
