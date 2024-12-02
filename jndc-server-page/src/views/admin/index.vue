<script setup lang="ts">
import { computed, type CSSProperties, ref } from "vue";

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
  handleRest,
  tableData,
  loading,
  columns,
  tableSize,
  pagination,
  loadingConfig,
  adaptiveConfig,
  onSizeChange,
  onCurrentChange
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

    <div style="width: 100%; text-align: right; padding: 5px">
      <el-button type="text" @click="openAddDialog">新增</el-button>
    </div>

    <!--表格-->
    <pure-table
      ref="waterRef"
      row-key="id"
      style="margin-top: 15px"
      :data="tableData"
      :columns="columns"
      stripe
      adaptive
      :adaptiveConfig="adaptiveConfig"
      border
      :size="tableSize as any"
      :loading="loading"
      :loading-config="loadingConfig"
      :pagination="pagination"
      @page-size-change="onSizeChange"
      @page-current-change="onCurrentChange"
    />

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
