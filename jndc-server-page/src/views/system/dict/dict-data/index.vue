<script setup lang="ts">
import { ref, watch } from "vue";
import { useDictData } from "./hook";
import Delete from "@iconify-icons/ep/delete";
import EditPen from "@iconify-icons/ep/edit-pen";
import AddFill from "@iconify-icons/ri/add-circle-line";
import Refresh from "@iconify-icons/ep/refresh";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { PureTableBar } from "@/components/RePureTableBar";
import { deviceDetection } from "@pureadmin/utils";
import { statusOptions } from "../utils/enums";

defineOptions({
  name: "SystemDictData"
});
const {
  form,
  isShow,
  loading,
  columns,
  dataList,
  pagination,
  curRow,
  formRef,
  tableRef,
  selectedNum,
  onSearch,
  resetForm,
  openDialog,
  handleDelete,
  handleSizeChange,
  handleCurrentChange,
  handleSelectionChange,
  handleDrawerOpen,
  handleDrawerClose,
  handleSelectionCancel,
  handlebatchDelete
} = useDictData();

const props = defineProps({
  // 控制值
  dictDataDrawer: {
    type: Boolean,
    default: false
  },
  // 字典对象
  dictObj: {
    type: Object,
    default: null
  }
});
const emit = defineEmits(["updateDictDataDrawer"]);
const localDictDataDrawer = ref(props.dictDataDrawer);
watch(
  () => props.dictDataDrawer,
  newVal => {
    console.log("props.dictDataDrawer", newVal);
    localDictDataDrawer.value = newVal;
  }
);
// 监听本地值的变化
watch(localDictDataDrawer, newVal => {
  emit("updateDictDataDrawer", newVal);
});
</script>

<template>
  <el-drawer
    v-model="localDictDataDrawer"
    :title="props.dictObj.dictName"
    direction="ltr"
    size="75%"
    @open="handleDrawerOpen(props.dictObj)"
    @closed="handleDrawerClose"
  >
    <el-scrollbar>
      <el-form
        ref="formRef"
        :inline="true"
        :model="form"
        class="search-form bg-bg_color w-[99/100] pl-8 pt-[12px] overflow-auto"
      >
        <el-form-item label="数据项名称：" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入数据项名称"
            clearable
            class="!w-[180px]"
            @keyup.enter="onSearch"
          />
        </el-form-item>
        <el-form-item label="数据项值：" prop="value">
          <el-input
            v-model="form.value"
            placeholder="请输入数据项值"
            clearable
            class="!w-[180px]"
            @keyup.enter="onSearch"
          />
        </el-form-item>
        <el-form-item label="状态：" prop="status">
          <el-select
            v-model="form.status"
            placeholder="请选择状态"
            clearable
            class="!w-[150px]"
            @change="onSearch"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
          <template v-slot="{ size, dynamicColumns }">
            <div
              v-if="selectedNum > 0"
              v-motion-fade
              class="bg-[var(--el-fill-color-light)] w-full h-[46px] mb-2 pl-4 flex items-center"
            >
              <div class="flex-auto">
                <span
                  style="font-size: var(--el-font-size-base)"
                  class="text-[rgba(42,46,54,0.5)] dark:text-[rgba(220,220,242,0.5)]"
                >
                  已选 {{ selectedNum }} 项
                </span>
                <el-button type="primary" text @click="handleSelectionCancel">
                  取消选择
                </el-button>
              </div>

              <el-button
                type="danger"
                text
                class="mr-1"
                @click="handlebatchDelete"
              >
                批量删除
              </el-button>
            </div>
            <pure-table
              ref="tableRef"
              row-key="id"
              align-whole="center"
              showOverflowTooltip
              table-layout="auto"
              :loading="loading"
              :size="size"
              adaptive
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
    </el-scrollbar>
    <template #footer>
      <div style="flex: auto">
        <el-button
          :icon="useRenderIcon(AddFill)"
          type="primary"
          @click="openDialog()"
          >新增</el-button
        >
      </div>
    </template>
  </el-drawer>
</template>

<style lang="scss" scoped></style>
