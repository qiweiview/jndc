import { reactive } from "vue";
import type { FormRules } from "element-plus";
import { REGEXP_EMAIL, REGEXP_PWD } from "@/views/system/user/utils/rule";

/** 角色校验 */
export const formRules = reactive(<FormRules>{
  newPassword: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入密码"));
        } else if (!REGEXP_PWD.test(value)) {
          callback(new Error("密码格式必须在6至20个字符之间且不能包含中文"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  newEmail: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入邮箱"));
        } else if (!REGEXP_EMAIL.test(value)) {
          callback(new Error("邮箱格式不正确"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ],
  // 验证码为6位（数字）
  verificationCode: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入验证码"));
        } else if (!/^\d{6}$/.test(value)) {
          callback(new Error("验证码为6位（数字）"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
});
