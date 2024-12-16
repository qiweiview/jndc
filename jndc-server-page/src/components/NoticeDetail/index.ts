import { computed, h, ref } from "vue";
import dayjs from "dayjs";
import { message as toast } from "@/utils/message";
import { getNoticeDetail, setRead } from "@/api/system/notice/notice";
import NoticeContent from "./Content.vue"; // Adjust the import path as necessary
import { debounce, deviceDetection } from "@pureadmin/utils";
import { ElButton } from "element-plus";
import type { DialogOptions } from "@/components/ReDialog/type";
import { addDialog } from "@/components/ReDialog";

export function openDetail(
  notice: any,
  options: DialogOptions,
  isUserOpen = false,
  emit?: (event: string, ...args: any[]) => void
) {
  const detail = ref(notice || {});
  const contentHeight = ref("67vh");
  console.log("notice", notice);
  const metadataItems = computed(() =>
    [
      h(
        "span",
        { style: { color: "var(--el-color-primary)" } },
        `发布人: ${detail.value.creator}`
      ),
      h(
        "span",
        { style: { color: "var(--el-color-primary)" } },
        `发布于: ${dayjs(detail.value.createTime).format("YYYY-MM-DD HH:mm:ss")}`
      ),
      detail.value.modifier &&
        h(
          "span",
          { style: { color: "var(--el-color-primary)" } },
          `修改人: ${detail.value.modifier}`
        ),
      detail.value.updateTime &&
        h(
          "span",
          { style: { color: "var(--el-color-primary)" } },
          `修改于: ${dayjs(detail.value.updateTime).format("YYYY-MM-DD HH:mm:ss")}`
        )
    ].filter(Boolean)
  );

  addDialog({
    width: "65%",
    center: true,
    top: "3vh",
    title: detail.value.title,
    closeOnClickModal: true,
    fullscreen: deviceDetection(),
    fullscreenIcon: true,
    open: async () => {
      const res = await getNoticeDetail(notice.id);
      if (res.code == 0) {
        detail.value = res.data;
        console.log("detail.value", detail.value);
      }
    },
    contentRenderer() {
      return h(
        "div",
        {
          style: {
            overflowY: "auto" // Add vertical scrollbar if content exceeds max height
          }
        },
        [
          h(
            "div",
            {
              style: {
                marginBottom: "10px",
                borderBottom: "1px solid #e0e0e0",
                paddingBottom: "10px",
                padding: "10px",
                display: "flex",
                justifyContent:
                  metadataItems.value.length > 2
                    ? "space-between"
                    : "space-around",
                gap: "10px",
                flexWrap: "wrap"
              }
            },
            metadataItems.value
          ),
          h(NoticeContent, {
            content: detail.value.content,
            contentHeight: contentHeight.value
          })
        ]
      );
    },
    fullscreenCallBack({ options }) {
      contentHeight.value = options.fullscreen ? "76vh" : "67vh";
    },
    footerRenderer() {
      const handleReadClick = debounce(
        async () => {
          if (notice.readStatus == 1) {
            toast("通知已读", { type: "warning" });
            return;
          }
          const res = await setRead(detail.value.id);
          if (res.code == 0) {
            toast("设为已读成功", { type: "success" });
            notice.readStatus = 1;
            if (emit) emit("read-success", detail.value);
          }
        },
        4000,
        true
      );
      return isUserOpen
        ? h(
            "div",
            {
              style: {
                display: "flex",
                justifyContent: "center",
                marginTop: "10px"
              }
            },
            [
              h(
                ElButton,
                {
                  type: "success",
                  plain: true,
                  text: true,
                  bg: true,
                  onClick: handleReadClick
                },
                {
                  default: () => (notice.readStatus == 0 ? "设为已读" : "已读")
                }
              )
            ]
          )
        : null;
    },
    ...options // 其他配置选项
  });
}
