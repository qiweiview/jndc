<script setup lang="ts">
import { ref } from "vue";
import type { FormInstance } from "element-plus";
import Motion from "@/views/login/utils/motion";
import { ruleForm } from "@/views/login/index";
import { loginRules } from "@/views/login/utils/rule";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import User from "@iconify-icons/ri/user-3-fill";
import Lock from "@iconify-icons/ri/lock-fill";
import { onLogin } from "@/views/login/index";
import { useRouter } from "vue-router";

const ruleFormRef = ref<FormInstance>();
const loading = ref(false);
const router = useRouter();

const doLoginOperation = (formEl: FormInstance | undefined) => {
  onLogin(formEl, loading, router, false);
};
</script>

<template>
  <el-form ref="ruleFormRef" :model="ruleForm" :rules="loginRules" size="large">
    <Motion :delay="100">
      <el-form-item
        :rules="[
          {
            required: true,
            message: '请输入账号',
            trigger: 'blur'
          }
        ]"
        prop="username"
      >
        <el-input
          v-model="ruleForm.username"
          clearable
          placeholder="账号"
          :prefix-icon="useRenderIcon(User)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="150">
      <el-form-item prop="password">
        <el-input
          v-model="ruleForm.password"
          clearable
          show-password
          placeholder="密码"
          :prefix-icon="useRenderIcon(Lock)"
        />
      </el-form-item>
    </Motion>

    <Motion :delay="250">
      <el-button
        class="w-full mt-4"
        size="default"
        type="primary"
        :loading="loading"
        @click="doLoginOperation(ruleFormRef)"
      >
        登录
      </el-button>
    </Motion>
  </el-form>
</template>
