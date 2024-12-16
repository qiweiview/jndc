import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 密码正则（密码格式必须在6至20个字符之间） */
export const REGEXP_PWD = /^.{6,20}$/;

/** 登录校验 */
const loginRules = reactive(<FormRules>{
  password: [
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入密码"));
        } else if (!REGEXP_PWD.test(value)) {
          callback(new Error("密码长度必须在6至20个字符之间"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
});

export { loginRules };
