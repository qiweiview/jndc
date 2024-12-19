<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    bindServerHost: null,
    bindServerPort: null,
    clientName: null,
    clientRemark: null,
    clientStatus: null,
    createTime: null,
    disguisedProtocol: null,
    id: null,
    uniqueId: null,
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
    <el-form-item label="服务主机：" prop="bindServerHost">
      <el-input
        v-model="newFormInline.bindServerHost"
        autocomplete="off"
        clearable
        placeholder="请输入服务主机"
      />
    </el-form-item>
    <el-form-item label="服务端口：" prop="bindServerPort">
      <el-input
        v-model="newFormInline.bindServerPort"
        autocomplete="off"
        clearable
        placeholder="请输入服务端口"
      />
    </el-form-item>
    <el-form-item label="客户端名称：" prop="clientName">
      <el-input
        v-model="newFormInline.clientName"
        autocomplete="off"
        clearable
        placeholder="请输入客户端名称"
      />
    </el-form-item>
    <el-form-item label="客户端备注：" prop="clientRemark">
      <el-input
        v-model="newFormInline.clientRemark"
        autocomplete="off"
        clearable
        placeholder="请输入客户端备注"
      />
    </el-form-item>
    <el-form-item label="客户端状态：" prop="clientStatus">
      <el-input
        v-model="newFormInline.clientStatus"
        autocomplete="off"
        clearable
        placeholder="请输入客户端状态"
      />
    </el-form-item>
    <el-form-item label="伪装协议：" prop="disguisedProtocol">
      <el-input
        v-model="newFormInline.disguisedProtocol"
        autocomplete="off"
        clearable
        placeholder="请输入伪装协议"
      />
    </el-form-item>
  </el-form>
</template>
