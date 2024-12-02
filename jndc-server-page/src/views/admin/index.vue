<script setup lang="ts">
import { computed, type CSSProperties, ref } from "vue";

//表格内容
import { searchContent, tableContent } from "./index";

import dialog_form_columns from "./dialog-form-columns";

import {
  type FieldValues,
  PlusDialogForm,
  PlusSearch
} from "plus-pro-components";

//定义弹框变量
const visible = ref(false);

//弹框表单
const dialogForm = ref<FieldValues>({});

//水印
const waterRef = ref();

const { form, searchColumn, handleChange, handleSearch, handleRest } =
  searchContent();

//表格配置信息
const {
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

    <div style="width: 100%; text-align: right; padding: 15px">
      <el-button type="text" @click="openAddDialog">新增</el-button>
    </div>
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

    <PlusDialogForm
      v-model:visible="visible"
      v-model="dialogForm"
      :form="{ columns: dialog_form_columns }"
    />
  </el-card>
</template>
