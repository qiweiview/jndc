<template>
  <van-nav-bar title="登陆" left-arrow :onClickLeft="toHomePage">
    <template v-slot:left>
      <van-icon name="wap-home" />
    </template>
  </van-nav-bar>
</template>
<script setup lang="ts">
import router from "@/router";
const toHomePage = () => {
  router.push("/");
};
</script>

<style scoped lang="scss"></style>
