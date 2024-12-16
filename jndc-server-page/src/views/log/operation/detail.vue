<script setup lang="tsx">
import { ref, watch } from "vue";
import "vue-json-pretty/lib/styles.css";
import VueJsonPretty from "vue-json-pretty";
import Order from "@iconify-icons/ri/order-play-line";
import dayjs from "dayjs";
import { getDictDataName, DictCode } from "@/utils/dict";
const props = defineProps({
  data: {
    type: Object,
    default: () => {}
  }
});
console.log("props.data", props.data);
const dataList = ref([
  {
    title: "请求参数",
    name: "operParam",
    data: JSON.parse(props.data.operParam)
  },
  {
    title: "返回参数",
    name: "jsonResult",
    data: JSON.parse(props.data.jsonResult)
  }
]);
</script>

<template>
  <div>
    <el-scrollbar>
      <el-descriptions :column="5" border>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              日志编号
            </div>
          </template>
          {{ data.id }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              操作模块
            </div>
          </template>
          {{ data.title }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />

              操作人
            </div>
          </template>

          <el-tag>{{ data.operUsername }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />

              操作状态
            </div>
          </template>
          <el-tag :type="data.status == 0 ? 'success' : 'danger'">
            {{ data.status == 0 ? "成功" : "失败" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />

              操作人类别
            </div>
          </template>
          {{
            getDictDataName(
              DictCode.OPER_OPERATOR_TYPE,
              data.operatorType.toString()
            )
          }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              请求接口
            </div>
          </template>
          {{ data.operUrl }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              操作IP
            </div>
          </template>
          {{ data.operIp }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              操作地点
            </div>
          </template>
          {{ data.operLocation }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              操作时间
            </div>
          </template>
          {{ dayjs(data.operTime).format("YYYY-MM-DD HH:mm:ss") }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              耗时
            </div>
          </template>
          {{ data.costTime }} 毫秒
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              操作业务类型
            </div>
          </template>
          {{
            getDictDataName(
              DictCode.OPER_BUSINESS_TYPE,
              data.businessType.toString()
            )
          }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              请求方式
            </div>
          </template>
          {{ data.requestMethod }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template #label>
            <div class="cell-item">
              <IconifyIconOffline :icon="Order" class="mr-2" />
              操作方法
            </div>
          </template>
          {{ data.method }}
        </el-descriptions-item>
      </el-descriptions>
    </el-scrollbar>
    <el-tabs :modelValue="'jsonResult'" type="border-card" class="mt-4">
      <el-tab-pane
        v-for="(item, index) in dataList"
        :key="index"
        :name="item.name"
        :label="item.title"
      >
        <el-scrollbar max-height="calc(100vh - 240px)">
          <vue-json-pretty v-model:data="item.data" />
        </el-scrollbar>
      </el-tab-pane>
      <el-tab-pane v-if="data.status == 1" name="errorMsg" label="异常信息">
        <el-scrollbar max-height="calc(100vh - 240px)">
          {{ data.errorMsg }}
        </el-scrollbar>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="scss" scoped>
.cell-item {
  display: flex;
  align-items: center;
}
</style>
