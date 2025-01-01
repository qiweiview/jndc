<script setup lang="ts">
import { contentTypes } from "./enums";
import { defineProps, defineEmits, computed } from "vue";

// 接收父组件绑定的对象
const props = defineProps({
  modelValue: {
    type: Object
  }
});

// 发出更新事件
const emit = defineEmits(["update:modelValue", "dataChange"]);

// 创建一个中间变量，双向绑定到父组件
const proxyValue = computed({
  get: () => props.modelValue,
  set: newValue => emit("update:modelValue", newValue)
});

const updateValue = (key: string, value: any) => {
  emit("update:modelValue", { ...proxyValue.value, [key]: value });

  const currentData = {
    bindType: proxyValue.value.bindType,
    mockData: proxyValue.value.mockData
  };
  // 触发 dataChange 事件，传递字符串值
  emit("dataChange", currentData);
};
</script>

<template>
  <el-form-item label="content-type：" prop="bindType">
    <el-select
      v-model="proxyValue.bindType"
      @change="value => updateValue('bindType', value)"
    >
      <el-option
        v-for="item in contentTypes"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </el-form-item>
  <el-form-item label="mock内容：" prop="mockData">
    <el-input
      v-model="proxyValue.mockData"
      :rows="6"
      resize="none"
      type="textarea"
      placeholder="输入mock内容"
      @input="value => updateValue('mockData', value)"
    />
  </el-form-item>
</template>

<style scoped lang="scss"></style>
