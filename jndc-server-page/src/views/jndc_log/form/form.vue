<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    id: null,
    logContent: null,
    logTime: null,
    logType: null,
    sourceId: null,
    idString: null
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
  <el-form ref="ruleFormRef" :model="newFormInline" :rules="formRules">
    <el-form-item label="日志内容：" prop="logContent">
      <el-input
        v-model="newFormInline.logContent"
        autocomplete="off"
        clearable
        placeholder="请输入日志内容"
      />
    </el-form-item>
    <el-form-item label="创建日期：" prop="logTime">
      <el-input
        v-model="newFormInline.logTime"
        autocomplete="off"
        clearable
        placeholder="请输入创建日期"
      />
    </el-form-item>
    <el-form-item label="日志类型：" prop="logType">
      <el-input
        v-model="newFormInline.logType"
        autocomplete="off"
        clearable
        placeholder="请输入日志类型"
      />
    </el-form-item>
    <el-form-item label="来源id：" prop="sourceId">
      <el-input
        v-model="newFormInline.sourceId"
        autocomplete="off"
        clearable
        placeholder="请输入来源id"
      />
    </el-form-item>
  </el-form>
</template>
