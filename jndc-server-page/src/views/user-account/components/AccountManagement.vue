<script setup lang="ts">
import { reactive, ref, watch } from "vue";
import { message } from "@/utils/message";
import { deviceDetection, isAllEmpty } from "@pureadmin/utils";
import { zxcvbn } from "@zxcvbn-ts/core";
import {
  sendEmailCode,
  sendPwdCode,
  updateEmail,
  updatePassword
} from "@/api/auth/user-account/account";
import { useUserStoreHook } from "@/store/modules/user";
import { formRules } from "../rolus";

defineOptions({
  name: "AccountManagement"
});
const list = ref([
  {
    title: "账户密码",
    illustrate: "修改您的账户密码以确保安全",
    button: "修改"
  },

  {
    title: "绑定邮箱",
    illustrate: "已绑定邮箱：" + useUserStoreHook().email,
    button: "修改"
  },
  {
    title: "绑定手机",
    illustrate: "已经绑定手机：" + useUserStoreHook().phone,
    button: "修改"
  }
]);
const dialogVisible = ref(false);
const dialogTitle = ref("");
const pwdFormRef = ref();
const pwdForm = reactive({
  newPassword: "",
  verificationCode: ""
});
const pwdProgress = [
  { color: "#e74242", text: "非常弱" },
  { color: "#EFBD47", text: "弱" },
  { color: "#ffa500", text: "一般" },
  { color: "#1bbf1b", text: "强" },
  { color: "#008000", text: "非常强" }
];
// 当前密码强度（0-4）
const curScore = ref();
const emailFormRef = ref();
const emailForm = reactive({
  newEmail: "",
  verificationCode: ""
});
const isShowPwdTime = ref(false);
const isShowEmailTime = ref(false);
const disabledSendEmail = ref(false);
const countdownPwd = reactive({
  timerId: null, //定时器id
  countdownTime: 0 // 定时器初始值
});
const countdownEmail = reactive({
  timerId: null, //定时器id
  countdownTime: 0 // 定时器初始值
});
function onClick(item) {
  console.log("onClick", item.title);
  switch (item.title) {
    case "账户密码":
      dialogTitle.value = "修改" + item.title;
      dialogVisible.value = true;
      break;
    case "绑定邮箱":
      dialogTitle.value = "修改" + item.title;
      dialogVisible.value = true;
      disabledSendEmail.value = true;
      break;
    case "绑定手机":
      message("此功能暂未开放", { type: "warning" });
      break;
    default:
      break;
  }
}
function handleUpdate() {
  switch (dialogTitle.value) {
    case "修改账户密码":
      handleUpdatePwd();
      break;
    case "修改绑定邮箱":
      handleUpdateEmail();
      break;
    case "修改绑定手机":
      message("此功能暂未开放", { type: "warning" });
      break;
    default:
      break;
  }
}
async function sendCodeForPwd() {
  const res = await sendPwdCode();
  if (res.code == 0) {
    countdownPwd.countdownTime = 60;
    countdownPwd.timerId = setInterval(() => {
      countdownPwd.countdownTime--;
      if (countdownPwd.countdownTime <= 0) {
        isShowPwdTime.value = false;
        clearInterval(countdownPwd.timerId);
      }
    }, 1000);
    isShowPwdTime.value = true;
    message("验证码发送成功", { type: "success" });
  }
}
async function sendCodeForEmail() {
  const res = await sendEmailCode(emailForm.newEmail);
  if (res.code == 0) {
    countdownEmail.countdownTime = 60;
    countdownEmail.timerId = setInterval(() => {
      countdownEmail.countdownTime--;
      if (countdownEmail.countdownTime <= 0) {
        isShowEmailTime.value = false;
        clearInterval(countdownEmail.timerId);
      }
    }, 1000);
    isShowEmailTime.value = true;
    message("验证码发送成功", { type: "success" });
  }
}
function handleUpdatePwd() {
  pwdFormRef.value.validate(async valid => {
    if (valid) {
      const res = await updatePassword(pwdForm);
      if (res.code == 0) {
        message("密码修改成功", { type: "success" });
        dialogVisible.value = false;
      }
    }
  });
}
function handleUpdateEmail() {
  emailFormRef.value.validate(async valid => {
    if (valid) {
      const res = await updateEmail(emailForm);
      if (res.code == 0) {
        message("邮箱修改成功", { type: "success" });
        useUserStoreHook().SET_EMAIL(emailForm.newEmail);

        dialogVisible.value = false;
      }
    }
  });
}
function handleClose() {
  dialogVisible.value = false;
  pwdFormRef.value.resetFields();
  emailFormRef.value.resetFields();
}
watch(
  pwdForm,
  ({ newPassword }) =>
    (curScore.value = isAllEmpty(newPassword) ? -1 : zxcvbn(newPassword).score)
);
watch(emailForm, ({ newEmail }) => {
  // 验证邮箱是否是正确的格式
  disabledSendEmail.value =
    !/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(newEmail);
});
</script>

<template>
  <div>
    <div
      :class="[
        'min-w-[180px]',
        deviceDetection() ? 'max-w-[100%]' : 'max-w-[70%]'
      ]"
    >
      <h3 class="my-8">账户管理</h3>
      <div v-for="(item, index) in list" :key="index">
        <div class="flex items-center">
          <div class="flex-1">
            <p>{{ item.title }}</p>
            <el-text class="mx-1" type="info">{{ item.illustrate }}</el-text>
          </div>
          <el-button type="primary" text @click="onClick(item)">
            {{ item.button }}
          </el-button>
        </div>
        <el-divider />
      </div>
    </div>
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550"
      top="5%"
      :before-close="handleClose"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form
        v-if="dialogTitle == '修改账户密码'"
        ref="pwdFormRef"
        :model="pwdForm"
        :rules="formRules"
      >
        <el-form-item label="密 码：" prop="newPassword">
          <el-input
            v-model="pwdForm.newPassword"
            placeholder="请输入新密码"
            clearable
          />
        </el-form-item>
        <div class="flex justify-center my-2">
          <div
            v-for="(item, idx) in pwdProgress"
            :key="idx"
            class="w-[19vw]"
            :style="idx !== 0 ? 'margin-left: 4px' : ''"
          >
            <ElProgress
              striped
              striped-flow
              :duration="curScore === idx ? 6 : 0"
              :percentage="curScore >= idx ? 100 : 0"
              :color="item.color"
              :stroke-width="10"
              :show-text="false"
            />
            <p
              class="text-center"
              :style="curScore === idx ? { color: item.color } : ''"
            >
              {{ item.text }}
            </p>
          </div>
        </div>

        <el-row>
          <el-col :span="18">
            <el-form-item label="验证码：" prop="verificationCode">
              <el-input
                v-model="pwdForm.verificationCode"
                :placeholder="`向${useUserStoreHook().email}发送验证码`"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col
            :span="6"
            style="display: flex !important; justify-content: end"
          >
            <el-button
              v-optimize="{
                event: 'click',
                fn: sendCodeForPwd,
                immediate: true,
                timeout: 5000
              }"
              bg
              text
              type="success"
              :disabled="isShowPwdTime"
            >
              <span v-if="!isShowPwdTime">获取验证码</span>
              <span v-else>{{ countdownPwd.countdownTime }}s后重新获取</span>
            </el-button>
          </el-col>
        </el-row>
      </el-form>
      <el-form
        v-if="dialogTitle == '修改绑定邮箱'"
        ref="emailFormRef"
        :model="emailForm"
        :rules="formRules"
      >
        <el-form-item label="邮 箱：" prop="newEmail">
          <el-input
            v-model="emailForm.newEmail"
            placeholder="请输入新邮箱"
            clearable
          />
        </el-form-item>
        <el-row>
          <el-col :span="18">
            <el-form-item label="验证码：" prop="verificationCode">
              <el-input
                v-model="emailForm.verificationCode"
                placeholder="请输入验证码"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col
            :span="6"
            style="display: flex !important; justify-content: end"
          >
            <el-button
              v-optimize="{
                event: 'click',
                fn: sendCodeForEmail,
                immediate: true,
                timeout: 5000
              }"
              bg
              text
              type="success"
              :disabled="isShowEmailTime || disabledSendEmail"
            >
              <span v-if="!isShowEmailTime">获取验证码</span>
              <span v-else>{{ countdownEmail.countdownTime }}s后重新获取</span>
            </el-button>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button text bg @click="dialogVisible = false">取消</el-button>
          <el-button text bg type="primary" @click="handleUpdate">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.el-divider--horizontal {
  border-top: 0.1px var(--el-border-color) var(--el-border-style);
}
</style>
