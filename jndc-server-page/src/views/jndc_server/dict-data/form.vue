<script setup lang="ts">
import { ref } from "vue";
import { dictDataformRules } from "../utils/rule";
import { DictDataFormProps } from "../utils/types";

const props = withDefaults(defineProps<DictDataFormProps>(), {
  formInline: () => ({})
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
    :rules="dictDataformRules"
    label-width="110px"
  />
</template>
