<script setup lang="ts">
import { contentTypes } from "./enums";
import { defineProps, defineEmits, computed } from "vue";
import { booleans } from "@/common/baseTypes";

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
    contentType: proxyValue.value.contentType,
    mockData: proxyValue.value.mockData,
    useSSL: proxyValue.value.useSSL
  };

  let defaultMockData;
  if ("contentType" === key) {
    // 自动填充对应类型的 mock 数据
    if ("application/json" === value) {
      defaultMockData = '{"hello":"world"}';
    } else if ("application/xml" === value) {
      defaultMockData = "<xml><hello>world</hello></xml>";
    } else if ("text/html" === value) {
      defaultMockData = "<html><body>hello world</body></html>";
    } else if ("text/plain" === value) {
      defaultMockData = "hello world";
    }
    currentData.mockData = defaultMockData;
    updateValue("mockData", defaultMockData);
  }

  // 触发 dataChange 事件，传递字符串值
  emit("dataChange", currentData);
};
</script>

<template>
  <el-form-item label="是否使用证书：" prop="useSSL">
    <el-radio-group
      v-model="proxyValue.useSSL"
      @change="value => updateValue('useSSL', value)"
    >
      <el-radio
        v-for="item in booleans"
        :key="item.value"
        :value="item.value"
        border
      >
        {{ item.label }}
      </el-radio>
    </el-radio-group>
  </el-form-item>
  <el-form-item label="Content-Type：" prop="contentType">
    <el-select
      v-model="proxyValue.contentType"
      @change="value => updateValue('contentType', value)"
    >
      <el-option
        v-for="item in contentTypes"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </el-form-item>
  <el-form-item label="Mock内容：" prop="mockData">
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
