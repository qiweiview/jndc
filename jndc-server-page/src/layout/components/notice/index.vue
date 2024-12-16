<script setup lang="ts">
import { provide, reactive, ref } from "vue";
import { noticesType } from "./type";
import NoticeList from "./noticeList.vue";
import Bell from "@iconify-icons/ep/bell";
import { getNoticeListByUser } from "@/api/system/notice/notice";

const noticesNum = ref(0);
const notices = ref(noticesType);
const activeKey = ref(noticesType[0].key);
const pagination = reactive({
  total: 0,
  size: 5,
  current: 1,
  type: null // 1 通知 2 公告
});
async function getNotices(isLoadMore = false) {
  pagination.type = Number(activeKey.value);
  if (isLoadMore) {
    pagination.current++;
  } else {
    pagination.current = 1;
  }
  const res = await getNoticeListByUser(pagination);
  noticesNum.value = res.data.countVO.noReadCount;
  res.data.records.forEach((item: any) => {
    // 去掉html标签
    item.content = item.content.replace(/<[^>]+>/g, "");
  });
  // countVO.totalCount为通知与公告的总数，total为通知或公告的总数
  switch (activeKey.value) {
    // 通知
    case "1":
      if (isLoadMore) {
        res.data.records.forEach((item: any) => {
          notices.value[0].list.push(item);
        });
      } else {
        notices.value[0].list = res.data.records;
      }
      notices.value[0].total = res.data.total;
      notices.value[1].total = res.data.countVO.totalCount - res.data.total;
      break;
    // 公告
    case "2":
      if (isLoadMore) {
        res.data.records.forEach((item: any) => {
          notices.value[1].list.push(item);
        });
      } else {
        notices.value[1].list = res.data.records;
      }
      notices.value[1].total = res.data.total;
      notices.value[0].total = res.data.countVO.totalCount - res.data.total;
      break;
  }
}
function loadMore() {
  getNotices(true);
}
getNotices();
// 给子组件提供方法
provide("refresh", getNotices);
</script>

<template>
  <el-dropdown trigger="click" placement="bottom-end">
    <span class="dropdown-badge navbar-bg-hover select-none">
      <el-badge :value="noticesNum" :max="99" :hidden="noticesNum === 0">
        <span class="header-notice-icon">
          <IconifyIconOffline :icon="Bell" />
        </span>
      </el-badge>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-tabs
          v-model="activeKey"
          :stretch="true"
          class="dropdown-tabs"
          :style="{ width: notices.length === 0 ? '200px' : '330px' }"
          @tab-change="getNotices(false)"
        >
          <el-empty
            v-if="notices.length === 0"
            description="暂无消息"
            :image-size="60"
          />
          <span v-else>
            <template v-for="item in notices" :key="item.key">
              <el-tab-pane
                :label="`${item.name}(${item.total})`"
                :name="`${item.key}`"
              >
                <el-scrollbar max-height="330px">
                  <div class="noticeList-container">
                    <NoticeList :list="item.list" />
                    <el-divider v-if="item.list.length < item.total">
                      <el-text
                        type="success"
                        style="cursor: pointer"
                        @click="loadMore"
                        >加载更多</el-text
                      ></el-divider
                    >
                  </div>
                </el-scrollbar>
              </el-tab-pane>
            </template>
          </span>
        </el-tabs>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style lang="scss" scoped>
.dropdown-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 48px;
  margin-right: 10px;
  cursor: pointer;

  .header-notice-icon {
    font-size: 18px;
  }
}

.dropdown-tabs {
  .noticeList-container {
    padding: 15px 24px 0;
  }

  :deep(.el-tabs__header) {
    margin: 0;
  }

  :deep(.el-tabs__nav-wrap)::after {
    height: 1px;
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0 36px;
  }
}
</style>
