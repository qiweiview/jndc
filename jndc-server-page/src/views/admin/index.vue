<script setup lang="ts">
import { computed, type CSSProperties, ref } from "vue";
import { PureTable } from "@pureadmin/table";
import { PureTableBar } from "@/components/RePureTableBar";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import Password from "@iconify-icons/ri/lock-password-line";
import More from "@iconify-icons/ep/more-filled";
import Delete from "@iconify-icons/ep/delete";

//表格内容
import { tableContent } from "./index";

import { PlusDialogForm, PlusSearch } from "plus-pro-components";

//水印
const waterRef = ref();

//表格配置信息
const {
  visible,
  dialogForm,
  dialogConfirm,
  dialog_rule,
  dialog_form_columns,
  form,
  searchColumn,
  handleChange,
  handleSearch,
  tableData,
  loading,
  columns,
  pagination,
  loadingConfig,
  adaptiveConfig,
  onSizeChange,
  onCurrentChange,
  handleDelete,
  handleRest,
  resetPasswordConfirm
} = tableContent(waterRef);

//打开新增弹框
const openAddDialog = () => {
  visible.value = true;
};

//动态样式
const elStyle = computed((): CSSProperties => {
  return {
    width: "83vw",
    justifyContent: "start"
  };
});
</script>
<template>
  <el-card shadow="never" :style="elStyle">
    <!--搜索框-->
    <PlusSearch
      v-model="form"
      reset-text="重置"
      search-text="搜索"
      retract-text="收起"
      expand-text="展开"
      :columns="searchColumn"
      :show-number="2"
      label-width="80"
      label-position="right"
      @change="handleChange"
      @search="handleSearch"
      @reset="handleRest"
    />

    <!--表格-->
    <PureTableBar title="" :columns="columns">
      <template #buttons>
        <el-button link type="success" @click="openAddDialog">新增</el-button>
      </template>
      <template v-slot="{ size, dynamicColumns }">
        <!--表格-->
        <PureTable
          ref="waterRef"
          row-key="id"
          style="margin-top: 15px"
          :data="tableData"
          :size="size"
          :columns="dynamicColumns"
          stripe
          adaptive
          :adaptiveConfig="adaptiveConfig"
          border
          :loading="loading"
          :loading-config="loadingConfig"
          :pagination="pagination"
          @page-size-change="onSizeChange"
          @page-current-change="onCurrentChange"
        >
          <template #operation="{ row }">
            <el-button
              size="small"
              type="primary"
              link
              :icon="useRenderIcon(Delete)"
              @click="handleDelete(row)"
            >
              删除
            </el-button>

            <el-dropdown>
              <el-button
                class="ml-3 mt-[2px]"
                link
                type="primary"
                :size="size"
                :icon="useRenderIcon(More)"
              />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>
                    <el-button
                      link
                      type="primary"
                      :size="size"
                      :icon="useRenderIcon(Password)"
                      @click="resetPasswordConfirm(row)"
                    >
                      重置密码
                    </el-button>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </PureTable>
      </template>
    </PureTableBar>

    <!--新增弹窗-->
    <PlusDialogForm
      v-model:visible="visible"
      v-model="dialogForm"
      title="新增"
      cancel-text="取消"
      confirm-text="确定"
      :form="{ columns: dialog_form_columns, rules: dialog_rule }"
      @cancel="visible = false"
      @confirm="dialogConfirm"
    />
  </el-card>
</template>
