<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./rule";
import { FormProps, MockMetaData } from "./types";
import { serverAppStatus } from "./enums";
import { serverAppType } from "./enums";
import formJndcClient from "./form-jndc-client.vue";
import formJndcMock from "./form-jndc-mock.vue";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    bindHost: null,
    bindPort: null,
    bindStatus: null,
    createTime: null,
    id: null,
    serverId: null,
    sourceClientId: null,
    sourceServiceId: null,
    idString: null,
    metaData: null
  })
});

let metaData;
if (props.formInline.metaData) {
  metaData = ref(JSON.parse(props.formInline.metaData) as MockMetaData);
} else {
  metaData = ref({
    mockData: "",
    mockType: ""
  });
}

const mockChange = (data: MockMetaData) => {
  newFormInline.value.metaData = JSON.stringify(data);
};

const ruleFormRef = ref();
const newFormInline = ref(props.formInline);

function getRef() {
  return ruleFormRef.value;
}
defineExpose({ getRef });
</script>

<template>
  <el-form ref="ruleFormRef" :model="newFormInline" :rules="formRules">
    <el-form-item label="监听域名：" prop="bindHost">
      <el-input
        v-model="newFormInline.bindHost"
        autocomplete="off"
        clearable
        placeholder="请输入监听域名"
      />
    </el-form-item>
    <el-form-item label="监听端口：" prop="bindPort">
      <el-input-number
        v-model="newFormInline.bindPort"
        autocomplete="off"
        clearable
        placeholder="请输入监听端口"
      />
    </el-form-item>
    <el-form-item label="绑定类型：" prop="bindType">
      <el-select
        v-model="newFormInline.bindType"
        placeholder="请选择绑定类型"
        clearable
      >
        <el-option
          v-for="item in serverAppType"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-form-item>
    <formJndcMock
      v-if="newFormInline.bindType == 'mock-server'"
      v-model="metaData"
      @dataChange="mockChange"
    />
    <!--    <el-form-item label="应用状态：" prop="serverStatus">-->
    <!--      <el-radio-group-->
    <!--        v-model="newFormInline.bindStatus"-->
    <!--        :disabled="-->
    <!--          newFormInline.bindStatus != 'listen' &&-->
    <!--          newFormInline.bindStatus != 'pause'-->
    <!--        "-->
    <!--      >-->
    <!--        <el-radio-->
    <!--          v-for="item in serverAppStatus"-->
    <!--          v-show="item.optional"-->
    <!--          :key="item.value"-->
    <!--          :value="item.value"-->
    <!--          border-->
    <!--        >-->
    <!--          {{ item.label }}-->
    <!--        </el-radio>-->
    <!--      </el-radio-group>-->
    <!--    </el-form-item>-->
  </el-form>
</template>
