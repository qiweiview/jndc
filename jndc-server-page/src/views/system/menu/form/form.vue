<script setup lang="ts">
import { ref } from "vue";
import ReCol from "@/components/ReCol";
import { formRules } from "../utils/rule";
import { FormProps } from "../utils/types";
import { IconSelect } from "@/components/ReIcon";
import Segmented from "@/components/ReSegmented";
import {
  frameLoadingOptions,
  keepAliveOptions,
  menuTypeOptions,
  visibleOptions
} from "../utils/enums";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    type: 0,
    higherMenuOptions: [],
    parentId: 0,
    title: "",
    name: "",
    path: "",
    component: "",
    sortOrder: 1,
    redirect: "",
    icon: "",
    extraIcon: "",
    enterTransition: "",
    leaveTransition: "",
    activePath: "",
    perms: "",
    frameSrc: "",
    frameLoading: 0,
    cacheFlag: 0,
    hiddenTag: false,
    fixedTag: false,
    visible: 0,
    showParent: false
  })
});

const ruleFormRef = ref();
const newFormInline = ref(props.formInline);
function getRef() {
  return ruleFormRef.value;
}
function menuTypeChange({ index, option }) {
  const { label, value } = option;
  console.log("option", option);
  switch (value) {
    // 目录
    case 0:
      newFormInline.value.enterTransition = "";
      newFormInline.value.leaveTransition = "";
      newFormInline.value.component = "";
      newFormInline.value.perms = "";
    // 菜单
    case 1:
      newFormInline.value.redirect = "";
      newFormInline.value.frameSrc = "";
      newFormInline.value.perms = "";
    // iframe
    case 2:
      newFormInline.value.component = "";
      newFormInline.value.perms = "";
    // 外链
    case 3:
      newFormInline.value.component = "";
      newFormInline.value.perms = "";
    // 按钮
    case 4:
      newFormInline.value.enterTransition = "";
      newFormInline.value.leaveTransition = "";
      newFormInline.value.component = "";
      newFormInline.value.redirect = "";
      newFormInline.value.frameSrc = "";
      newFormInline.value.name = "";
      newFormInline.value.path = "";
  }
}
defineExpose({ getRef });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="formRules"
    label-width="100px"
  >
    <el-row :gutter="30">
      <re-col>
        <el-form-item label="菜单类型：">
          <Segmented
            v-model="newFormInline.type"
            :options="menuTypeOptions"
            @change="menuTypeChange"
          />
        </el-form-item>
      </re-col>

      <re-col>
        <el-form-item label="上级菜单：">
          <el-cascader
            v-model="newFormInline.parentIdString"
            class="w-full"
            :options="newFormInline.higherMenuOptions"
            :props="{
              value: 'id',
              label: 'title',
              emitPath: false,
              checkStrictly: true
            }"
            clearable
            filterable
            placeholder="请选择上级菜单（不选则为顶级菜单）"
          >
            <template #default="{ node, data }">
              <span>{{ data.title }}</span>
              <span v-if="!node.isLeaf"> ({{ data.children.length }}) </span>
            </template>
          </el-cascader>
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="菜单名称：" prop="title">
          <el-input
            v-model="newFormInline.title"
            clearable
            placeholder="请输入菜单名称"
          />
        </el-form-item>
      </re-col>
      <re-col v-if="newFormInline.type !== 4" :value="12" :xs="24" :sm="24">
        <el-form-item
          :label="newFormInline.type !== 3 ? '路由名称：' : '外链地址：'"
          prop="name"
        >
          <el-input
            v-model="newFormInline.name"
            clearable
            :placeholder="
              newFormInline.type !== 3
                ? '如：SystemMenu'
                : '如：https://github.com'
            "
          />
        </el-form-item>
      </re-col>

      <re-col v-if="newFormInline.type !== 4" :value="12" :xs="24" :sm="24">
        <el-form-item label="路由路径：" prop="path">
          <el-input
            v-model="newFormInline.path"
            clearable
            placeholder="如：/system/menu"
          />
        </el-form-item>
      </re-col>
      <re-col v-if="newFormInline.type === 1" :value="12" :xs="24" :sm="24">
        <el-form-item label="组件路径：" prop="component">
          <el-input
            v-model="newFormInline.component"
            clearable
            placeholder="如：system/menu/index"
          />
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="菜单排序：">
          <el-input-number
            v-model="newFormInline.sortOrder"
            class="!w-full"
            :min="1"
            :max="9999"
            controls-position="right"
          />
        </el-form-item>
      </re-col>
      <re-col v-if="newFormInline.type === 0" :value="12" :xs="24" :sm="24">
        <el-form-item label="路由重定向：">
          <el-input
            v-model="newFormInline.redirect"
            clearable
            placeholder="请输入默认跳转地址"
          />
        </el-form-item>
      </re-col>

      <re-col v-if="newFormInline.type !== 4" :value="12" :xs="24" :sm="24">
        <el-form-item label="菜单图标：">
          <IconSelect v-model="newFormInline.icon" class="w-full" />
        </el-form-item>
      </re-col>

      <re-col v-if="newFormInline.type === 4" :value="12" :xs="24" :sm="24">
        <!-- 按钮级别权限设置 -->
        <el-form-item label="权限标识：" prop="perms">
          <el-input
            v-model="newFormInline.perms"
            clearable
            placeholder="请输入权限标识"
          />
        </el-form-item>
      </re-col>

      <re-col v-if="newFormInline.type === 2" :value="12" :xs="24" :sm="24">
        <!-- iframe -->
        <el-form-item label="链接地址：">
          <el-input
            v-model="newFormInline.frameSrc"
            clearable
            placeholder="如：https://github.com"
          />
        </el-form-item>
      </re-col>
      <re-col v-if="newFormInline.type === 2" :value="12" :xs="24" :sm="24">
        <el-form-item label="加载动画：">
          <Segmented
            :modelValue="newFormInline.frameLoading"
            :options="frameLoadingOptions"
            @change="
              ({ option: { value } }) => {
                newFormInline.frameLoading = Number(value);
              }
            "
          />
        </el-form-item>
      </re-col>

      <re-col :value="12" :xs="24" :sm="24">
        <el-form-item label="显示状态：">
          <Segmented
            :modelValue="newFormInline.visible"
            :options="visibleOptions"
            @change="
              ({ option: { value } }) => {
                newFormInline.visible = Number(value);
              }
            "
          />
        </el-form-item>
      </re-col>

      <re-col v-if="newFormInline.type === 1" :value="12" :xs="24" :sm="24">
        <el-form-item label="缓存页面：">
          <Segmented
            :modelValue="newFormInline.cacheFlag"
            :options="keepAliveOptions"
            @change="
              ({ option: { value } }) => {
                newFormInline.cacheFlag = Number(value);
              }
            "
          />
        </el-form-item>
      </re-col>
    </el-row>
  </el-form>
</template>
