<script setup lang="ts">
import { h, reactive, ref } from "vue";
import { message } from "@/utils/message";
import type { FormInstance } from "element-plus";
import ReCropperPreview from "@/components/ReCropperPreview";
import { deviceDetection } from "@pureadmin/utils";
import { useUserStoreHook } from "@/store/modules/user";
import { getAccountRole, updateInfo } from "@/api/auth/user-account/account";
import { useUpload } from "@/utils/upload/upload";
import { genderOptions } from "@/views/system/user/utils/enums";
import { formRules } from "@/views/system/user/utils/rule";
import { showDialog } from "@/components/HalcyonDialog";

defineOptions({
  name: "Profile"
});

const imgSrc = ref("");
const avatarInfo = ref();
const cropRef = ref();
const uploadRef = ref();
const isShow = ref(false);
const userInfoFormRef = ref<FormInstance>();
const { uploadFileByBack } = useUpload();
const userInfos = reactive({
  avatar: useUserStoreHook().avatar,
  nickname: useUserStoreHook().nickname,
  intro: useUserStoreHook().intro,
  birthday: useUserStoreHook().birthday,
  gender: useUserStoreHook().gender
});
const disableUpdateInfo = ref({
  username: useUserStoreHook().username,
  roleArr: []
});

const onChange = uploadFile => {
  const reader = new FileReader();
  reader.onload = e => {
    imgSrc.value = e.target.result as string;
    isShow.value = true;
  };
  reader.readAsDataURL(uploadFile.raw);
};

const handleClose = () => {
  cropRef.value.hidePopover();
  uploadRef.value.clearFiles();
  isShow.value = false;
};

const onCropper = fileInfo => {
  avatarInfo.value = fileInfo;
};

const handleSubmitImage = () => {
  const file = new File([avatarInfo.value.blob], avatarInfo.value.info.name, {
    type: avatarInfo.value.blob.type
  });
  uploadFileByBack(file).then((res: any) => {
    if (res.code == 0) {
      userInfos.avatar = res.data;
      message("上传成功", { type: "success" });
    } else {
      message(res.message, { type: "error" });
    }
    handleClose();
  });
};

// 更新信息
const onSubmit = async (formEl: FormInstance) => {
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      showDialog("提示", {
        contentRenderer: () =>
          h("div", { class: "text-[16px] mb-4 text-center" }, `确定要更新吗？`),
        beforeSure: async done => {
          const updateData = {
            avatar: userInfos.avatar,
            nickname: userInfos.nickname,
            intro: userInfos.intro,
            birthday: userInfos.birthday,
            gender: userInfos.gender
          };
          const res = await updateInfo(updateData);
          if (res.code == 0) {
            message("更新成功", { type: "success" });
            useUserStoreHook().SET_AVATAR(updateData.avatar);
            useUserStoreHook().SET_NICKNAME(updateData.nickname);
            useUserStoreHook().SET_INTRO(updateData.intro);
            useUserStoreHook().SET_BIRTHDAY(updateData.birthday);
            useUserStoreHook().SET_GENDER(updateData.gender);
            done();
          }
        },
        closeCallBack: ({ args }) => {}
      });
    } else {
      return fields;
    }
  });
};
async function getRole() {
  const res = await getAccountRole();
  if (res.code == 0) {
    disableUpdateInfo.value.roleArr = res.data;
  }
}
getRole();
</script>

<template>
  <div
    :class="[
      'min-w-[180px]',
      deviceDetection() ? 'max-w-[100%]' : 'max-w-[70%]'
    ]"
    class="user-account-profile"
  >
    <h3 class="my-8">个人信息</h3>
    <el-form
      ref="userInfoFormRef"
      :rules="formRules"
      label-position="top"
      :model="userInfos"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="头像">
            <el-avatar :size="80" :src="userInfos.avatar" />
            <el-upload
              ref="uploadRef"
              accept="image/*"
              action="#"
              :limit="1"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="onChange"
            >
              <!--              <el-button text bg type="primary" class="ml-4">-->
              <!--                <IconifyIconOffline :icon="uploadLine" />-->
              <!--                <span class="ml-2">更新头像</span>-->
              <!--              </el-button>-->
            </el-upload>
          </el-form-item></el-col
        >
        <el-col :span="8">
          <el-form-item label="用户名" prop="username">
            <div style="height: 80px; display: flex; align-items: center">
              <el-button text bg>
                {{ disableUpdateInfo.username }}
              </el-button>
            </div></el-form-item
          >
        </el-col>
        <el-col :span="8">
          <el-form-item label="角色" prop="username">
            <div style="height: 80px; width: 100%" class="item-center">
              <el-scrollbar>
                <div style="display: flex; width: 100%">
                  <el-button
                    v-for="item in disableUpdateInfo.roleArr"
                    :key="item.roleCode"
                    text
                    bg
                  >
                    {{ item.roleName }}（{{ item.roleCode }}）
                  </el-button>
                </div>
              </el-scrollbar>
            </div></el-form-item
          ></el-col
        >
      </el-row>

      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="userInfos.nickname" placeholder="请输入昵称" />
      </el-form-item>
      <el-form-item label="生日" prop="birthday">
        <el-date-picker
          v-model="userInfos.birthday"
          type="date"
          placeholder="请选择生日"
        />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="userInfos.gender">
          <el-radio
            v-for="item in genderOptions"
            :key="item.value"
            :value="item.value"
            border
          >
            <span class="flex justify-center items-center">
              <IconifyIconOnline :icon="item.icon" class="mr-1" />
              {{ item.label }}
            </span>
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="简介">
        <el-input
          v-model="userInfos.intro"
          placeholder="请输入简介"
          type="textarea"
          :autosize="{ minRows: 6, maxRows: 8 }"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-button type="primary" @click="onSubmit(userInfoFormRef)">
        更新信息
      </el-button>
    </el-form>
    <el-dialog
      v-model="isShow"
      width="40%"
      title="编辑头像"
      destroy-on-close
      :closeOnClickModal="false"
      :before-close="handleClose"
      :fullscreen="deviceDetection()"
    >
      <ReCropperPreview ref="cropRef" :imgSrc="imgSrc" @cropper="onCropper" />
      <template #footer>
        <div class="dialog-footer">
          <el-button bg text @click="handleClose">取消</el-button>
          <el-button bg text type="primary" @click="handleSubmitImage">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss">
.user-account-profile {
  .item-center {
    display: flex;
    align-items: center;
  }
  .el-scrollbar__wrap {
    @extend .item-center;
  }
}
</style>
