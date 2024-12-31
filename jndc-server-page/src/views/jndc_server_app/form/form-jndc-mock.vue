<script setup lang="ts">
import { defineProps, defineEmits, ref, watch } from "vue";

// Define the modelValue prop to receive the value from the parent
const props = defineProps({
  modelValue: {
    type: String,
    default: ""
  }
});

// Define the emit function to emit update:modelValue
const emit = defineEmits(["update:modelValue"]);

// Create a local state to manage the value
const localValue = ref(props.modelValue);

// Watch the local value and emit changes to the parent component
watch(localValue, newValue => {
  emit("update:modelValue", newValue);
});
</script>

<template>
  <el-input
    v-model="localValue"
    style="width: 240px"
    :rows="2"
    type="textarea"
    placeholder="Please input"
  />
</template>

<style scoped lang="scss"></style>
