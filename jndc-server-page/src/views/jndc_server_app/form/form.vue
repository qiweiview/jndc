<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    bindHost: null,
    bindPort: null,
    bindStatus: null,
    createTime: null,
    id: null,
    serverId: null,
    sourceClientId: null,
    sourceServiceId: null,
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
    <el-form-item label="监听域名：" prop="bindHost">
      <el-input
        v-model="newFormInline.bindHost"
        autocomplete="off"
        clearable
        placeholder="请输入监听域名"
      />
    </el-form-item>
    <el-form-item label="监听端口：" prop="bindPort">
      <el-input
        v-model="newFormInline.bindPort"
        autocomplete="off"
        clearable
        placeholder="请输入监听端口"
      />
    </el-form-item>
    <el-form-item label="监听状态：" prop="bindStatus">
      <el-input
        v-model="newFormInline.bindStatus"
        autocomplete="off"
        clearable
        placeholder="请输入监听状态"
      />
    </el-form-item>
    <el-form-item label="jndc服务id：" prop="serverId">
      <el-input
        v-model="newFormInline.serverId"
        autocomplete="off"
        clearable
        placeholder="请输入jndc服务id"
      />
    </el-form-item>
    <el-form-item label="来源客户端：" prop="sourceClientId">
      <el-input
        v-model="newFormInline.sourceClientId"
        autocomplete="off"
        clearable
        placeholder="请输入来源客户端"
      />
    </el-form-item>
    <el-form-item label="来源服务：" prop="sourceServiceId">
      <el-input
        v-model="newFormInline.sourceServiceId"
        autocomplete="off"
        clearable
        placeholder="请输入来源服务"
      />
    </el-form-item>
  </el-form>
</template>
