<script setup lang="ts">
import { useHook } from "./hook";
import { onMounted, ref } from "vue";
import { PureTableBar } from "@/components/RePureTableBar";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { deviceDetection } from "@pureadmin/utils";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import Refresh from "@iconify-icons/ep/refresh";
import AddFill from "@iconify-icons/ri/add-circle-line";
import stopCircleLine from "@iconify-icons/ri/stop-circle-line";

defineOptions({
  name: "jndcClientService"
});

const formRef = ref();
const tableRef = ref();

const props = defineProps({
  sourceIdString: {
    type: String
  }
});

const sourceIdString = props.sourceIdString;

const {
  form,
  isShow,
  loading,
  columns,
  dataList,
  pagination,
  unRegisterCheck,
  onSearch,
  openDialog,
  handleDelete,
  handleSizeChange,
  handleCurrentChange,
  handleSelectionChange
} = useHook(sourceIdString);

if (sourceIdString) {
  form.clientIdString = sourceIdString;
}
const onServer = ref(false);
onMounted(() => {
  const fullLocation = window.location.href;
  //判断以/server/jndc_client_service结尾
  if (fullLocation.endsWith("/server/jndc_client_service")) {
    onServer.value = true;
  }
});
</script>

<template>
  <div class="main">
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
            v-show="!onServer"
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
              <div v-show="!onServer">
                <el-button
                  v-show="row.serviceStatus != 'register'"
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
                  v-show="row.serviceStatus != 'register'"
                  class="reset-margin"
                  link
                  type="danger"
                  :size="size"
                  :icon="useRenderIcon(Delete)"
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
                <el-button
                  v-show="row.serviceStatus == 'register'"
                  link
                  type="danger"
                  :icon="useRenderIcon(stopCircleLine)"
                  @click="unRegisterCheck(row)"
                >
                  取消注册
                </el-button>
              </div>
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
