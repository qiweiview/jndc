<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
        id: null,
    createTime: null,
    updateTime: null,
    clientUniqueId: null,
    serviceName: null,
    serviceHost: null,
    servicePort: null,
    expectPort: null,
    serviceStatus: null,
    serviceProtocol: null,
    serviceMode: null,
    serviceUniqueId: null,
    serverUniqueId: null,
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
    <el-form-item label="客户端唯一id：" prop="clientUniqueId">
      <el-input
        v-model="newFormInline.clientUniqueId"
        autocomplete="off"
        clearable
        placeholder="请输入客户端唯一id"
      />
    </el-form-item>
    <el-form-item label="服务名称：" prop="serviceName">
      <el-input
        v-model="newFormInline.serviceName"
        autocomplete="off"
        clearable
        placeholder="请输入服务名称"
      />
    </el-form-item>
    <el-form-item label="服务主机：" prop="serviceHost">
      <el-input
        v-model="newFormInline.serviceHost"
        autocomplete="off"
        clearable
        placeholder="请输入服务主机"
      />
    </el-form-item>
    <el-form-item label="服务端口：" prop="servicePort">
      <el-input
        v-model="newFormInline.servicePort"
        autocomplete="off"
        clearable
        placeholder="请输入服务端口"
      />
    </el-form-item>
    <el-form-item label="期望端口：" prop="expectPort">
      <el-input
        v-model="newFormInline.expectPort"
        autocomplete="off"
        clearable
        placeholder="请输入期望端口"
      />
    </el-form-item>
    <el-form-item label="服务状态：" prop="serviceStatus">
      <el-input
        v-model="newFormInline.serviceStatus"
        autocomplete="off"
        clearable
        placeholder="请输入服务状态"
      />
    </el-form-item>
    <el-form-item label="服务协议：" prop="serviceProtocol">
      <el-input
        v-model="newFormInline.serviceProtocol"
        autocomplete="off"
        clearable
        placeholder="请输入服务协议"
      />
    </el-form-item>
    <el-form-item label="服务模式：" prop="serviceMode">
      <el-input
        v-model="newFormInline.serviceMode"
        autocomplete="off"
        clearable
        placeholder="请输入服务模式"
      />
    </el-form-item>
    <el-form-item label="服务唯一id：" prop="serviceUniqueId">
      <el-input
        v-model="newFormInline.serviceUniqueId"
        autocomplete="off"
        clearable
        placeholder="请输入服务唯一id"
      />
    </el-form-item>
    <el-form-item label="服务器唯一id：" prop="serverUniqueId">
      <el-input
        v-model="newFormInline.serverUniqueId"
        autocomplete="off"
        clearable
        placeholder="请输入服务器唯一id"
      />
    </el-form-item>
  </el-form>
</template>
