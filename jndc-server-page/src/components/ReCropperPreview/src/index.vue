<script setup lang="tsx">
import { computed, ref } from "vue";
import ReCropper from "@/components/ReCropper";
import { formatBytes } from "@pureadmin/utils";

defineOptions({
  name: "ReCropperPreview"
});

const props = defineProps({
  imgSrc: String
});

const emit = defineEmits(["cropper"]);

const infos = ref();
const popoverRef = ref();
const refCropper = ref();
const showPopover = ref(false);
const cropperImg = ref<string>("");
const defaultCropperImg = computed(() => {
  return props.imgSrc == "" ? "defaultStr" : props.imgSrc;
});
function onCropper({ base64, blob, info }) {
  infos.value = info;
  cropperImg.value = base64;
  emit("cropper", { base64, blob, info });
}

function hidePopover() {
  popoverRef.value.hide();
}
function onReadied() {
  showPopover.value = true;
}
defineExpose({ hidePopover });
</script>

<template>
  <div v-loading="!showPopover" element-loading-background="transparent">
    <el-popover
      ref="popoverRef"
      :visible="showPopover"
      placement="right"
      width="18vw"
    >
      <template #reference>
        <div class="w-[18vw]">
          <ReCropper
            ref="refCropper"
            style="border: 1px solid var(--el-border-color-light)"
            :src="defaultCropperImg"
            circled
            @cropper="onCropper"
            @readied="onReadied"
          />
          <p v-show="showPopover" class="mt-1 text-center">
            温馨提示：右键上方裁剪区可开启功能菜单
          </p>
        </div>
      </template>
      <div
        class="flex flex-wrap justify-center items-center text-center"
        style="height: 100%"
      >
        <el-image
          :src="cropperImg"
          style="width: 200px; height: 200px"
          :preview-src-list="Array.of(cropperImg)"
          fit="cover"
        />
        <div v-if="infos" class="mt-1">
          <p>
            图像大小：{{ parseInt(infos.width) }} ×
            {{ parseInt(infos.height) }}像素
          </p>
          <p>
            文件大小：{{ formatBytes(infos.size) }}（{{ infos.size }} 字节）
          </p>
        </div>
        <!-- 占位 -->
        <div v-else class="mt-1" style="height: 236.54px" />
      </div>
    </el-popover>
  </div>
</template>
