<script setup lang="ts">
import {ref} from "vue";
// https://plus-pro-components.com/components/dialog-form.html
import "plus-pro-components/es/components/dialog-form/style/css";
import {type FieldValues, PlusDialogForm, PlusForm} from "plus-pro-components";

import dialog_form_columns from "./dialog-columns";
import {handleChange, handleReset, handleSubmit, handleSubmitError, rules, state} from "./base-column";

const visible = ref(false);
const values = ref<FieldValues>({});

const handleOpen = () => {
  visible.value = true;
};
</script>

<template>
  <div>
    <el-button @click="handleOpen">打开弹窗表单</el-button>
    <PlusDialogForm
      v-model:visible="visible"
      v-model="values"
      :form="{ columns: dialog_form_columns }"
    />

    <PlusForm
      v-model="state"
      class="w-[450px] m-auto"
      :columns="dialog_form_columns"
      :rules="rules"
      label-position="right"
      @change="handleChange"
      @submit="handleSubmit"
      @submit-error="handleSubmitError"
      @reset="handleReset"
    />
  </div>
</template>
