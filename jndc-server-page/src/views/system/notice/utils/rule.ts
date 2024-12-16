import { reactive } from "vue";
import type { FormRules } from "element-plus";

/** 用户校验 */
export const formRules = reactive(<FormRules>{
  title: [{ required: true, message: "标题为必填项", trigger: "blur" }],
  status: [{ required: true, message: "状态为必填项", trigger: "blur" }],
  gender: [{ required: true, message: "性别为必填项", trigger: "blur" }],
  type: [{ required: true, message: "类型为必填项", trigger: "blur" }],
  roleIds: [{ required: true, message: "通知角色为必选项", trigger: "blur" }],
  content: [
    {
      required: true,
      validator: (rule, value, callback) => {
        // 检查是否包含图片或视频
        const containsMedia = /<img[^>]*>|<video[^>]*>.*?<\/video>/gi.test(
          value
        );
        // 去除 HTML 标签
        const strippedContent = value.replace(/<\/?[^>]+>/gi, "");
        // 如果内容中不包含图片或视频，检查去除标签后的内容是否为空
        if (!containsMedia && strippedContent.trim() === "") {
          callback(new Error("内容为必填项"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
});
