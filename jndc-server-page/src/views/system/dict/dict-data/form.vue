<script setup lang="ts">
import { ref } from "vue";
import { dictDataformRules } from "../utils/rule";
import { DictDataFormProps } from "../utils/types";
import { statusOptions } from "../utils/enums";

const props = withDefaults(defineProps<DictDataFormProps>(), {
  formInline: () => ({
    dictId: null,
    name: "",
    value: "",
    color: "",
    status: 0,
    sortOrder: 1,
    remark: ""
  })
});
const predefineColors = ["#409EFF", "#67C23A", "#E6A23C", "#F56C6C", "#909399"];
const ruleFormRef = ref();
const newFormInline = ref(props.formInline);

function getRef() {
  return ruleFormRef.value;
}

function colorChange(color: string = "") {
  const finalColor = color ?? ""; // 使用空字符串作为默认值
  newFormInline.value.color = finalColor;
  console.log("color", finalColor);
  console.log("newFormInline", newFormInline.value);
}

defineExpose({ getRef });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="dictDataformRules"
    label-width="110px"
  >
    <el-form-item label="数据项名称：" prop="name">
      <el-input
        v-model="newFormInline.name"
        clearable
        placeholder="请输入数据项名称"
      />
    </el-form-item>

    <el-form-item label="数据项值：" prop="value">
      <el-input
        v-model="newFormInline.value"
        clearable
        placeholder="请输入数据项值"
      />
    </el-form-item>
    <el-form-item label="状态：" prop="status">
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
    <el-form-item label="排序：" prop="sortOrder">
      <el-input-number v-model="newFormInline.sortOrder" :min="1" :max="9999" />
    </el-form-item>
    <el-form-item label="数据颜色：" prop="color">
      <el-color-picker
        v-model="newFormInline.color"
        :predefine="predefineColors"
        @change="colorChange"
      />
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
