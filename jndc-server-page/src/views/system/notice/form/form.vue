<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, shallowRef } from "vue";
import { formRules } from "../utils/rule";
import { FormProps } from "../utils/types";
import { statusOptions, typeOptions } from "../utils/enums";
import "@wangeditor/editor/dist/css/style.css";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import { message as toast } from "@/utils/message";
import { useUpload } from "@/utils/upload/upload";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    id: null,
    title: "",
    content: "",
    status: null,
    type: null,
    remark: "",
    allRoles: [],
    editorHeight: ""
  })
});
const { uploadFileByBack } = useUpload();
const mode = "default";
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();
const toolbarConfig = { excludeKeys: ["fullScreen"] };
const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {}
};
editorConfig.MENU_CONF["uploadImage"] = {
  // 上传之前触发
  onBeforeUpload(file: File) {
    const isImage = file.type.startsWith("image/");
    const isLt10M = file.size / 1024 / 1024 < 10;
    if (!isImage) {
      toast("上传的图片文件类型不符合", { type: "error" });
      return false;
    }
    if (!isLt10M) {
      toast("上传的图片文件大小不能超过 10MB", { type: "error" });
      return false;
    }
    return file;
  },
  // 自定义上传
  async customUpload(file: File, insertFn: any) {
    console.log("customUpload", file);
    const res: any = await uploadFileByBack(file);
    if (res.code == 0) {
      insertFn(res.data, "", res.data);
      toast("上传成功", { type: "success" });
    } else {
      toast("上传失败", { type: "error" });
    }
  },
  allowedFileTypes: ["image/png", "image/jpg", "image/jpeg"]
};
editorConfig.MENU_CONF["uploadVideo"] = {
  onBeforeUpload(file: File) {
    const isImage = file.type.startsWith("video/");
    const isLt15M = file.size / 1024 / 1024 < 15;
    if (!isImage) {
      toast("上传的视频文件类型不符合", { type: "error" });
      return false;
    }
    if (!isLt15M) {
      toast("上传的图片文件大小不能超过 15MB", { type: "error" });
      return isLt15M;
    }
    return file;
  },
  // 自定义上传
  async customUpload(file: File, insertFn: any) {
    console.log("customUpload", file);
    const res: any = await uploadFileByBack(file);
    if (res.code == 0) {
      insertFn(res.data, "");
      toast("上传成功", { type: "success" });
    } else {
      toast("上传失败", { type: "error" });
    }
  }
};
editorConfig.MENU_CONF["codeSelectLang"] = {
  // 代码语言
  codeLangs: [
    { text: "CSS", value: "css" },
    { text: "HTML", value: "html" },
    { text: "XML", value: "xml" },
    { text: "JavaScript", value: "javascript" },
    { text: "TypeScript", value: "typescript" },
    { text: "Java", value: "java" },
    { text: "Python", value: "python" },
    { text: "C", value: "c" },
    { text: "C++", value: "cpp" },
    { text: "CSharp", value: "csharp" },
    { text: "PHP", value: "php" },
    { text: "Ruby", value: "ruby" },
    { text: "Go", value: "go" },
    { text: "Shell", value: "shell" },
    { text: "Swift", value: "swift" },
    { text: "Kotlin", value: "kotlin" },
    { text: "Rust", value: "rust" },
    { text: "Bash", value: "bash" },
    { text: "Scala", value: "scala" },
    { text: "Sql", value: "sql" },
    { text: "Markdown", value: "markdown" },
    { text: "Yaml", value: "yaml" },
    { text: "JSON", value: "json" },
    { text: "Matlab", value: "matlab" }
    // 其他
  ]
};
const ruleFormRef = ref();
const newFormInline = ref(props.formInline);
let copyRoleOptions = [];
function searchRole(query) {
  newFormInline.value.allRoles = copyRoleOptions.filter(item =>
    item.roleName.includes(query)
  );
}
function getRef() {
  return ruleFormRef.value;
}
const handleCreated = editor => {
  // 记录 editor 实例，重要！
  editorRef.value = editor;
};
onMounted(() => {
  copyRoleOptions = props.formInline.allRoles;
});
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});
defineExpose({ getRef });
</script>

<template>
  <form>
    <!-- 套上一层form防止自动填充 -->
    <el-form
      ref="ruleFormRef"
      :model="newFormInline"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="标题：" prop="title">
        <el-input
          v-model="newFormInline.title"
          autocomplete="off"
          clearable
          placeholder="请输入标题"
        />
      </el-form-item>
      <el-row>
        <el-col :span="7">
          <el-form-item label="类型：" prop="type">
            <el-radio-group
              v-model="newFormInline.type"
              :disabled="newFormInline.id !== null"
            >
              <el-radio-button
                v-for="item in typeOptions"
                :key="item.value"
                :value="item.value"
              >
                <span class="flex">
                  <IconifyIconOnline :icon="item.icon" class="mr-1" size="24" />
                  {{ item.label }}
                </span>
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="8">
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
        </el-col>
        <el-col :span="9">
          <el-form-item
            v-if="newFormInline.type === 1"
            label="通知角色："
            prop="roleIds"
          >
            <el-select
              v-model="newFormInline.roleIds"
              placeholder="请选择"
              class="w-full"
              clearable
              multiple
              collapse-tags
              :max-collapse-tags="1"
              filterable
              :filter-method="searchRole"
            >
              <el-option
                v-for="(item, index) in newFormInline.allRoles"
                :key="index"
                :value="item.id"
                :label="item.roleName"
                :disabled="newFormInline.id !== null"
              >
                {{ item.roleName }}
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="内容：" prop="content">
        <div class="w-full">
          <Toolbar
            style="border-bottom: 1px solid #ccc"
            :editor="editorRef"
            :defaultConfig="toolbarConfig"
            :mode="mode"
          />
          <Editor
            v-model="newFormInline.content"
            style="width: 100%"
            :style="{ height: props.formInline.editorHeight }"
            :defaultConfig="editorConfig"
            :mode="mode"
            @onCreated="handleCreated"
          />
        </div>
        <el-divider />
      </el-form-item>
    </el-form>
  </form>
</template>
