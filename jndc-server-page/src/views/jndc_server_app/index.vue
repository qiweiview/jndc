<script setup lang="ts">
import { useHook } from "./hook";
import { ref } from "vue";
import { PureTableBar } from "@/components/RePureTableBar";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { deviceDetection } from "@pureadmin/utils";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import Refresh from "@iconify-icons/ep/refresh";
import AddFill from "@iconify-icons/ri/add-circle-line";
import VedioPlay from "@iconify-icons/ep/video-play";
import stopCircleLine from "@iconify-icons/ri/stop-circle-line";
import epMagnet from "@iconify-icons/ep/magnet";

defineOptions({
  name: "jndcServerApp"
});

const formRef = ref();
const tableRef = ref();

const {
  form,
  isShow,
  loading,
  columns,
  dataList,
  pagination,
  listenCheck,
  pauseCheck,
  onSearch,
  resetForm,
  openDialog,
  handleDelete,
  handleSizeChange,
  handleCurrentChange,
  handleSelectionChange
} = useHook();

const props = defineProps({
  id: {
    type: String
  }
});

//如果sourceIdString不为空则写入form
if (props.id) {
  form.serverIdString = props.id;
}

const openAddress = row => {
  let host = row.bindHost;
  if ("0.0.0.0" === host) {
    host = "localhost";
  }
  let protocol = "http://";

  if (row.metaData && JSON.parse(row.metaData).useSSL) {
    protocol = "https://";
  }
  //指定标签页打开
  window.open(`${protocol}${host}:${row.bindPort}`, "_jndc_server_app");
};
</script>

<template>
  <div class="main">
    <el-form
      ref="formRef"
      :inline="true"
      :model="form"
      class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto"
    >
      <el-form-item>
        <el-input
          v-model="form.serverIdString"
          placeholder="请输入服务器ID"
          clearable
          style="width: 200px"
          @change="onSearch"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :icon="useRenderIcon('ri:search-line')"
          :loading="loading"
          @click="onSearch"
        >
          搜索
        </el-button>
        <el-button :icon="useRenderIcon(Refresh)" @click="resetForm(formRef)">
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <div
      ref="contentRef"
      :class="['flex', deviceDetection() ? 'flex-wrap' : '']"
    >
      <PureTableBar
        :class="[isShow && !deviceDetection() ? '!w-[60vw]' : 'w-full']"
        style="transition: width 220ms cubic-bezier(0.4, 0, 0.2, 1)"
        title="列表"
        :columns="columns"
        @refresh="onSearch"
      >
        <template #buttons>
          <el-button
            type="primary"
            :icon="useRenderIcon(AddFill)"
            @click="openDialog()"
          >
            新增
          </el-button>
        </template>
        <template v-slot="{ size, dynamicColumns }">
          <pure-table
            ref="tableRef"
            row-key="id"
            align-whole="center"
            showOverflowTooltip
            table-layout="auto"
            :loading="loading"
            :size="size"
            adaptive
            :adaptiveConfig="{ offsetBottom: 108 }"
            :data="dataList"
            :columns="dynamicColumns"
            :pagination="pagination"
            :paginationSmall="size === 'small' ? true : false"
            :header-cell-style="{
              background: 'var(--el-fill-color-light)',
              color: 'var(--el-text-color-primary)'
            }"
            @selection-change="handleSelectionChange"
            @page-size-change="handleSizeChange"
            @page-current-change="handleCurrentChange"
          >
            <template #operation="{ row }">
              <el-button
                v-show="row.bindStatus == 'pause'"
                link
                type="success"
                :icon="useRenderIcon(VedioPlay)"
                @click="listenCheck(row)"
              >
                启动
              </el-button>
              <el-button
                v-show="row.bindStatus == 'listen'"
                link
                type="danger"
                :icon="useRenderIcon(stopCircleLine)"
                @click="pauseCheck(row)"
              >
                停止
              </el-button>
              <el-button
                v-show="row.bindStatus == 'listen'"
                link
                type="primary"
                :icon="useRenderIcon(epMagnet)"
                @click="openAddress(row)"
              >
                访问
              </el-button>
              <el-button
                v-show="row.bindStatus == 'pause'"
                class="reset-margin"
                link
                type="primary"
                :size="size"
                :icon="useRenderIcon(EditPen)"
                @click="openDialog('修改', row)"
              >
                修改
              </el-button>
              <el-button
                v-show="row.bindStatus == 'pause'"
                class="reset-margin"
                link
                type="danger"
                :size="size"
                :icon="useRenderIcon(Delete)"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </template>
          </pure-table>
        </template>
      </PureTableBar>
    </div>
  </div>
</template>

<style scoped lang="scss">
:deep(.el-dropdown-menu__item i) {
  margin: 0;
}

.main-content {
  margin: 24px 24px 0 !important;
}

.search-form {
  :deep(.el-form-item) {
    margin-bottom: 12px;
  }
}
</style>
