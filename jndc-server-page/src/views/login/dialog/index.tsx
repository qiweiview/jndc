import { addDialog } from "@/components/ReDialog/index";
import forms from "./login-form.vue";
import type { FormProps } from "./login-form.vue";
import { ref } from "vue";

const opened = ref(false);

export const openLoginDialog = () => {
  if (opened.value) {
    console.log("1已经打开了", opened.value);
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

  console.log("2已经打开了", opened.value);
  opened.value = true;
};
