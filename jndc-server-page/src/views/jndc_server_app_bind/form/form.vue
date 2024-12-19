<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
        bindPort: null,
    bindSource: null,
    bindStatus: null,
    createTime: null,
    id: null,
    latestBindResult: null,
    updateTime: null,
    idString:null
  })
});

const ruleFormRef = ref();
const newFormInline = ref(props.formInline);

function getRef() {
  return ruleFormRef.value;
}
defineExpose({ getRef });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="formRules"
  >
    <el-form-item label="绑定端口：" prop="bindPort">
      <el-input
        v-model="newFormInline.bindPort"
        autocomplete="off"
        clearable
        placeholder="请输入绑定端口"
      />
    </el-form-item>
    <el-form-item label="绑定来源：" prop="bindSource">
      <el-input
        v-model="newFormInline.bindSource"
        autocomplete="off"
        clearable
        placeholder="请输入绑定来源"
      />
    </el-form-item>
    <el-form-item label="绑定状态：" prop="bindStatus">
      <el-input
        v-model="newFormInline.bindStatus"
        autocomplete="off"
        clearable
        placeholder="请输入绑定状态"
      />
    </el-form-item>
    <el-form-item label="最后绑定结果：" prop="latestBindResult">
      <el-input
        v-model="newFormInline.latestBindResult"
        autocomplete="off"
        clearable
        placeholder="请输入最后绑定结果"
      />
    </el-form-item>
  </el-form>
</template>
