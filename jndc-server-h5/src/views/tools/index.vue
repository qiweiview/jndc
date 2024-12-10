<template>
  <van-search value="{{ value }}" placeholder="请输入搜索关键词" />
  <van-list
    v-model:loading="loading"
    :finished="finished"
    finished-text="没有更多了"
    @load="onLoad"
  >
    <van-cell v-for="item in list" :key="item" :title="item" />
  </van-list>
</template>
<script setup lang="ts">
import { ref } from "vue";
const value = ref("");
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const onLoad = () => {
  loading.value = true;
  setTimeout(() => {
    list.value.push(...[1, 2, 3, 4, 5]);
    loading.value = false;
    if (list.value.length >= 5) {
      finished.value = true;
    }
  }, 1000);
};
</script>
