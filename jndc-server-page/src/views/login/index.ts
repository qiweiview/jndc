import { reactive, type Ref } from "vue";
import type { FormInstance } from "element-plus";
import { useUserStoreHook } from "@/store/modules/user";
import { getTopMenu, initRouter } from "@/router/utils";
import { message } from "@/utils/message";
import { useDictStoreHook } from "@/store/modules/dict";
import type { Router } from "vue-router";

export const ruleForm = reactive({
  username: import.meta.env.VITE_DEFAULT_NAME,
  password: import.meta.env.VITE_DEFAULT_PASSWORD
});

export const onLogin = async (
  formEl: FormInstance | undefined,
  loading: Ref<boolean>,
  router: Router,
  loginPage: boolean
) => {
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      loading.value = true;
      useUserStoreHook()
        .loginByUsername({
          username: ruleForm.username,
          password: ruleForm.password
        })
        .then(res => {
          if (res.code == 0) {
            // 获取后端路由
            if (loginPage) {
              //todo 登陆页
              return initRouter().then(() => {
                loading.value = true;
                router.push(getTopMenu(true).path).then(() => {
                  message("登录成功", { type: "success" });
                  useDictStoreHook().loadDicts();
                });
              });
            } else {
              //刷新当前页面
              console.log("刷新当前页面");
              window.location.reload();
            }
          } else {
            message(`登录失败,${res.message}`, { type: "error" });
          }
        })
        .finally(() => (loading.value = false));
    } else {
      return fields;
    }
  });
};
