<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps } from "./types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    belongId: null,
    createTime: null,
    effectEndTime: null,
    effectStartTime: null,
    id: null,
    ruleName: null,
    ruleStatus: null,
    ruleType: null,
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
    <el-form-item label="截至时间：" prop="effectEndTime">
      <el-input
        v-model="newFormInline.effectEndTime"
        autocomplete="off"
        clearable
        placeholder="请输入截至时间"
      />
    </el-form-item>
    <el-form-item label="起始时间：" prop="effectStartTime">
      <el-input
        v-model="newFormInline.effectStartTime"
        autocomplete="off"
        clearable
        placeholder="请输入起始时间"
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
  </el-form>
</template>
