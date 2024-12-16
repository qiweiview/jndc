<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "../utils/rule";
import { FormProps } from "../utils/types";
import { statusOptions } from "../utils/enums";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    dictName: "",
    dictCode: "",
    status: 0,
    remark: ""
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
    label-width="100px"
  >
    <el-form-item label="字典名称：" prop="dictName">
      <el-input
        v-model="newFormInline.dictName"
        clearable
        placeholder="请输入字典名称"
      />
    </el-form-item>

    <el-form-item label="字典编码：" prop="dictCode">
      <el-input
        v-model="newFormInline.dictCode"
        clearable
        placeholder="请输入字典编码"
        :disabled="newFormInline.id !== null"
      />
    </el-form-item>
    <el-form-item label="字典状态：" prop="status">
      <el-radio-group v-model="newFormInline.status">
        <el-radio
          v-for="item in statusOptions"
          :key="item.value"
          :value="item.value"
          border
        >
          {{ item.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="备注：">
      <el-input
        v-model="newFormInline.remark"
        placeholder="请输入备注信息"
        type="textarea"
      />
    </el-form-item>
  </el-form>
</template>
