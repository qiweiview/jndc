<script setup lang="ts">
import {computed, type CSSProperties, ref} from "vue";

//表格内容
import {searchContent, tableContent} from "./index";

import dialog_form_columns from "./dialog-form-columns";

import {type FieldValues, PlusDialogForm, PlusSearch} from "plus-pro-components";

const visible = ref(false);

const values = ref<FieldValues>({});

const waterRef = ref();

const {form, searchColumn, handleChange, handleSearch, handleRest} =
  searchContent();

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

for (let i = 0; i < 50; i++) {
  tableData.value.push({
    date: "2016-05-02",
    name: "王小虎",
    address: "上海市普陀区金沙江路 1518 弄"
  });
}

const addRow = () => {
  visible.value = true;
};

const elStyle = computed((): CSSProperties => {
  return {
    width: "85vw",
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

    <PlusDialogForm
      v-model:visible="visible"
      v-model="values"
      :form="{ columns: dialog_form_columns }"
    />

    <el-button type="success" size="small" @click="addRow">create</el-button>
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
  </el-card>
</template>
