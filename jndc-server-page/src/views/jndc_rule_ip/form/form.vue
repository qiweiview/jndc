<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    belongId: null,
    createTime: null,
    id: null,
    ipAddress: null,
    ruleName: null,
    ruleStatus: null,
    ruleType: null,
    samplingLength: null,
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
    <el-form-item label="所属id：" prop="belongId">
      <el-input
        v-model="newFormInline.belongId"
        autocomplete="off"
        clearable
        placeholder="请输入所属id"
      />
    </el-form-item>
    <el-form-item label="ip地址：" prop="ipAddress">
      <el-input
        v-model="newFormInline.ipAddress"
        autocomplete="off"
        clearable
        placeholder="请输入ip地址"
      />
    </el-form-item>
    <el-form-item label="规则名称：" prop="ruleName">
      <el-input
        v-model="newFormInline.ruleName"
        autocomplete="off"
        clearable
        placeholder="请输入规则名称"
      />
    </el-form-item>
    <el-form-item label="是否生效：" prop="ruleStatus">
      <el-input
        v-model="newFormInline.ruleStatus"
        autocomplete="off"
        clearable
        placeholder="请输入是否生效"
      />
    </el-form-item>
    <el-form-item label="规则类型：" prop="ruleType">
      <el-input
        v-model="newFormInline.ruleType"
        autocomplete="off"
        clearable
        placeholder="请输入规则类型"
      />
    </el-form-item>
    <el-form-item label="采样长度：" prop="samplingLength">
      <el-input
        v-model="newFormInline.samplingLength"
        autocomplete="off"
        clearable
        placeholder="请输入采样长度"
      />
    </el-form-item>
  </el-form>
</template>
