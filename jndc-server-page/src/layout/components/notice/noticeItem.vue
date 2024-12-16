<script setup lang="ts">
import { ListItem } from "./type";
import { inject, nextTick, PropType, ref } from "vue";
import { useNav } from "@/layout/hooks/useNav";
import { deviceDetection } from "@pureadmin/utils";
import dayjs from "dayjs";
import "dayjs/locale/zh-cn";
import relativeTime from "dayjs/plugin/relativeTime";
import { openDetail } from "@/components/NoticeDetail";

dayjs.extend(relativeTime); // 相对时间
dayjs.locale("zh-cn"); // 使用本地化语言
const props = defineProps({
  noticeItem: {
    type: Object as PropType<ListItem>,
    default: () => {}
  }
});

const titleRef = ref(null);
const titleTooltip = ref(false);
const { tooltipEffect } = useNav();
const isMobile = deviceDetection();
const getNotice = inject("refresh") as Function;
function hoverTitle() {
  nextTick(() => {
    titleRef.value?.scrollWidth > titleRef.value?.clientWidth
      ? (titleTooltip.value = true)
      : (titleTooltip.value = false);
  });
}
function openNotice() {
  openDetail(props.noticeItem, {}, true, (event, notice) => {
    getNotice();
  });
}
</script>

<template>
  <div
    class="notice-container border-b-[1px] border-solid border-[#f0f0f0] dark:border-[#303030]"
  >
    <div class="notice-container-text">
      <div class="notice-text-title text-[#000000d9] dark:text-white">
        <el-tooltip
          popper-class="notice-title-popper"
          :effect="tooltipEffect"
          :disabled="!titleTooltip"
          :content="props.noticeItem.title"
          placement="top-start"
          :enterable="!isMobile"
        >
          <div
            ref="titleRef"
            class="notice-title-content"
            @mouseover="hoverTitle"
            @click="openNotice"
          >
            {{ props.noticeItem.title }}
          </div>
        </el-tooltip>
        <el-tag
          v-if="props.noticeItem?.readStatus === 0"
          size="small"
          class="notice-title-extra"
          type="danger"
        >
          未读
        </el-tag>
        <el-tag v-else size="small" class="notice-title-extra" type="success">
          已读
        </el-tag>
      </div>

      <div class="notice-text-description">
        {{ props.noticeItem.content }}
      </div>
      <div class="notice-text-footer text-[#00000073] dark:text-white">
        <span>
          {{ dayjs().to(dayjs(props.noticeItem.createTime)) }}
        </span>
        <span>{{ props.noticeItem.creator }}</span>
      </div>
    </div>
  </div>
</template>

<style>
.notice-title-popper {
  max-width: 238px;
}
</style>
<style scoped lang="scss">
.notice-container {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 12px 0;

  // border-bottom: 1px solid #f0f0f0;

  .notice-container-avatar {
    margin-right: 16px;
    background: #fff;
  }

  .notice-container-text {
    display: flex;
    flex: 1;
    flex-direction: column;
    justify-content: space-between;

    .notice-text-title {
      display: flex;
      margin-bottom: 8px;
      font-size: 14px;
      font-weight: 400;
      line-height: 1.5715;
      cursor: pointer;

      .notice-title-content {
        flex: 1;
        width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .notice-title-extra {
        float: right;
        margin-top: -1.5px;
        font-weight: 400;
      }
    }

    .notice-text-description,
    .notice-text-footer {
      font-size: 12px;
      line-height: 1.5715;
    }

    .notice-text-description {
      display: -webkit-box;
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .notice-text-footer {
      margin-top: 4px;
      display: flex;
      justify-content: space-between;
    }
  }
}
</style>
