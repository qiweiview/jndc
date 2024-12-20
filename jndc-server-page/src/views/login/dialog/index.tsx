import { addDialog } from "@/components/ReDialog/index";
import forms from "./login-form.vue";
import type { FormProps } from "./login-form.vue";
import { ref } from "vue";

const opened = ref(false);

export const openLoginDialog = () => {
  if (opened.value) {
    return;
  }

  addDialog({
    width: "20%",
    title: "登陆",
    draggable: true,
    hideFooter: true,
    closeOnPressEscape: false,
    contentRenderer: () => forms,
    props: {
      // 赋默认值
      formInline: {
        user: "",
        password: ""
      }
    }
  });

  opened.value = true;
};
