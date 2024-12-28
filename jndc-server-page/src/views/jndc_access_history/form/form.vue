<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    createTime: null,
    destination: null,
    destinationId: null,
    id: null,
    packageSampling: null,
    remoteIp: null,
    remotePort: null,
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
    <el-form-item label="访问目标：" prop="destination">
      <el-input
        v-model="newFormInline.destination"
        autocomplete="off"
        clearable
        placeholder="请输入访问目标"
      />
    </el-form-item>
    <el-form-item label="目标id：" prop="destinationId">
      <el-input
        v-model="newFormInline.destinationId"
        autocomplete="off"
        clearable
        placeholder="请输入目标id"
      />
    </el-form-item>
    <el-form-item label="数据采样：" prop="packageSampling">
      <el-input
        v-model="newFormInline.packageSampling"
        autocomplete="off"
        clearable
        placeholder="请输入数据采样"
      />
    </el-form-item>
    <el-form-item label="ip地址：" prop="remoteIp">
      <el-input
        v-model="newFormInline.remoteIp"
        autocomplete="off"
        clearable
        placeholder="请输入ip地址"
      />
    </el-form-item>
    <el-form-item label="端口：" prop="remotePort">
      <el-input
        v-model="newFormInline.remotePort"
        autocomplete="off"
        clearable
        placeholder="请输入端口"
      />
    </el-form-item>
  </el-form>
</template>
