import type { DialogOptions } from "@/components/ReDialog/type";
import { addDialog } from "@/components/ReDialog";
import { h } from "vue";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";

export function showDialog(title: "提示" | "警告", options: DialogOptions) {
  // 定义默认的颜色和图标样式
  let titleColor = "var(--el-color-warning)";
  let icon = "twemoji:face-with-raised-eyebrow";

  // 根据 title 设置不同的颜色和图标
  if (title === "提示") {
    titleColor = "var(--el-color-warning)";
    icon = "emojione-monotone:drooling-face";
  } else if (title === "警告") {
    titleColor = "var(--el-color-danger)";
    icon = "emojione-monotone:face-screaming-in-fear";
  }

  // 调用 addDialog 方法，并动态设置头部的颜色和图标
  addDialog({
    width: "25%",
    center: true,
    closeOnClickModal: true,
    headerRenderer({ titleId }) {
      return h(
        "h3",
        {
          id: titleId,
          style: {
            color: titleColor,
            display: "flex",
            alignItems: "center",
            justifyContent: "center"
          }
        },
        [
          h(
            useRenderIcon(icon, {
              style: {
                marginRight: "10px",
                fontSize: "25px",
                color: titleColor
              }
            })
          ),
          `系统${title}` // 根据传入的 title 显示标题文字
        ]
      );
    },
    ...options // 其他配置选项
  });
}
