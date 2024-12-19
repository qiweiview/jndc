<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    clientId: null,
    connectTime: null,
    createTime: null,
    id: null,
    interruptTime: null,
    sourceIp: null,
    sourcePort: null,
    updateTime: null,
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
    <el-form-item label="客户端id：" prop="clientId">
      <el-input
        v-model="newFormInline.clientId"
        autocomplete="off"
        clearable
        placeholder="请输入客户端id"
      />
    </el-form-item>
    <el-form-item label="连接时间：" prop="connectTime">
      <el-input
        v-model="newFormInline.connectTime"
        autocomplete="off"
        clearable
        placeholder="请输入连接时间"
      />
    </el-form-item>
    <el-form-item label="中断时间：" prop="interruptTime">
      <el-input
        v-model="newFormInline.interruptTime"
        autocomplete="off"
        clearable
        placeholder="请输入中断时间"
      />
    </el-form-item>
    <el-form-item label="来源ip：" prop="sourceIp">
      <el-input
        v-model="newFormInline.sourceIp"
        autocomplete="off"
        clearable
        placeholder="请输入来源ip"
      />
    </el-form-item>
    <el-form-item label="来源端口：" prop="sourcePort">
      <el-input
        v-model="newFormInline.sourcePort"
        autocomplete="off"
        clearable
        placeholder="请输入来源端口"
      />
    </el-form-item>
  </el-form>
</template>
