<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";
import { jndcClientStatusCondition, autoReConnect } from "./enums";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    autoReconnect: null,
    clientName: null,
    clientRemark: null,
    clientStatus: null,
    createTime: null,
    disguisedProtocol: null,
    id: null,
    reconnectInterval: null,
    reconnectMaxTimes: null,
    serverHost: null,
    serverPort: null,
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
  <el-form
    ref="ruleFormRef"
    label-position="left"
    :model="newFormInline"
    :rules="formRules"
  >
    <el-form-item label="客户端名称：" prop="clientName">
      <el-input
        v-model="newFormInline.clientName"
        autocomplete="off"
        clearable
        placeholder="请输入客户端名称"
      />
    </el-form-item>
    <el-form-item label="服务主机：" prop="serverHost">
      <el-input
        v-model="newFormInline.serverHost"
        autocomplete="off"
        clearable
        placeholder="请输入服务主机"
      />
    </el-form-item>
    <el-form-item label="服务端口：" prop="serverPort">
      <el-input
        v-model="newFormInline.serverPort"
        autocomplete="off"
        clearable
        placeholder="请输入服务端口"
      />
    </el-form-item>
    <!--    <el-form-item label="客户端状态：" prop="serverStatus">
      <el-radio-group v-model="newFormInline.clientStatus">
        <el-radio
          v-for="item in jndcClientStatusCondition(newFormInline.clientStatus)"
          v-show="item.exist"
          :key="item.value"
          :value="item.value"
          :disabled="item.chooseAble"
          border
        >
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>-->
    <el-form-item label="自动重连：" prop="autoReconnect">
      <el-radio-group v-model="newFormInline.autoReconnect">
        <el-radio
          v-for="item in autoReConnect"
          v-show="item.optional"
          :key="item.value"
          :value="item.value"
          border
        >
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item
      v-if="newFormInline.autoReconnect == 1"
      label="重连间隔："
      prop="reconnectInterval"
    >
      <el-input
        v-model="newFormInline.reconnectInterval"
        autocomplete="off"
        clearable
        placeholder="请输入重连间隔"
      />
    </el-form-item>
    <el-form-item
      v-if="newFormInline.autoReconnect == 1"
      label="重连次数限制："
      prop="reconnectMaxTimes"
    >
      <el-input
        v-model="newFormInline.reconnectMaxTimes"
        autocomplete="off"
        clearable
        placeholder="请输入重连次数限制"
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
  </el-form>
</template>
