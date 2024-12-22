<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";
import { autoRegisterOption, jndcClientServiceStatus } from "./enums";
import Segmented from "@/components/ReSegmented";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    autoRegister: null,
    clientId: null,
    createTime: null,
    expectPort: null,
    id: null,
    serviceHost: null,
    serviceMode: null,
    serviceName: null,
    servicePort: null,
    serviceProtocol: null,
    serviceStatus: null,
    serviceUniqueId: null,
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
    <el-form-item label="服务状态：" prop="serviceProtocol">
      <el-radio-group
        v-model="newFormInline.serviceStatus"
        :disabled="
          newFormInline.serviceStatus != 'register' &&
          newFormInline.serviceStatus != 'unregister'
        "
      >
        <el-radio
          v-for="item in jndcClientServiceStatus"
          v-show="item.optional"
          :key="item.value"
          :value="item.value"
          border
        >
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="是否自动注册：" prop="autoRegister">
      <Segmented
        :modelValue="newFormInline.autoRegister"
        :options="autoRegisterOption"
        @change="
          ({ option: { value } }) => {
            newFormInline.autoRegister = Number(value);
          }
        "
      />
    </el-form-item>
  </el-form>
</template>
