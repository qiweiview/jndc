<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    autoRegister: null,
    belongClientId: null,
    clientUniqueId: null,
    createTime: null,
    id: null,
    serviceHost: null,
    serviceName: null,
    servicePort: null,
    serviceStatus: null,
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
    <el-form-item label="是否自动注册：" prop="autoRegister">
      <el-input
        v-model="newFormInline.autoRegister"
        autocomplete="off"
        clearable
        placeholder="请输入是否自动注册"
      />
    </el-form-item>
    <el-form-item label="所属客户端id：" prop="belongClientId">
      <el-input
        v-model="newFormInline.belongClientId"
        autocomplete="off"
        clearable
        placeholder="请输入所属客户端id"
      />
    </el-form-item>
    <el-form-item label="客户端唯一id：" prop="clientUniqueId">
      <el-input
        v-model="newFormInline.clientUniqueId"
        autocomplete="off"
        clearable
        placeholder="请输入客户端唯一id"
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
    <el-form-item label="服务名称：" prop="serviceName">
      <el-input
        v-model="newFormInline.serviceName"
        autocomplete="off"
        clearable
        placeholder="请输入服务名称"
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
    <el-form-item label="服务状态：" prop="serviceStatus">
      <el-input
        v-model="newFormInline.serviceStatus"
        autocomplete="off"
        clearable
        placeholder="请输入服务状态"
      />
    </el-form-item>
  </el-form>
</template>
