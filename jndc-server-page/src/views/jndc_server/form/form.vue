<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";
import { jndcServerStatus } from "./enums";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    bindPort: null,
    bindHost: null,
    bindTactics: null,
    createTime: null,
    id: null,
    serverName: null,
    serverRemark: null,
    serverStatus: null,
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
    <el-form-item label="监听域名：" prop="bindHost">
      <el-input
        v-model="newFormInline.bindHost"
        autocomplete="off"
        clearable
        placeholder="请输入监听域名"
      />
    </el-form-item>
    <el-form-item label="监听端口：" prop="bindPort">
      <el-input-number
        v-model="newFormInline.bindPort"
        autocomplete="off"
        clearable
        placeholder="端口"
      />
    </el-form-item>
    <el-form-item label="服务名称：" prop="serverName">
      <el-input
        v-model="newFormInline.serverName"
        autocomplete="off"
        clearable
        placeholder="请输入服务名称"
      />
    </el-form-item>
    <el-form-item label="服务状态：" prop="serverStatus">
      <el-radio-group
        v-model="newFormInline.serverStatus"
        :disabled="
          newFormInline.serverStatus != 'listen' &&
          newFormInline.serverStatus != 'pause'
        "
      >
        <el-radio
          v-for="item in jndcServerStatus"
          v-show="item.optional"
          :key="item.value"
          :value="item.value"
          border
        >
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="服务备注：" prop="serverRemark">
      <el-input
        v-model="newFormInline.serverRemark"
        autocomplete="off"
        clearable
        placeholder="请输入服务备注"
      />
    </el-form-item>
  </el-form>
</template>
