<template>
  <div class="container">
    <router-view class="content"></router-view>
    <nut-tabbar class="tabbar" v-model="active" @tab-switch="tabSwitch">
      <nut-tabbar-item tab-title="Home">
        <template #icon>
          <Home></Home>
        </template>
      </nut-tabbar-item>
      <nut-tabbar-item tab-title="Category">
        <template #icon>
          <Category></Category>
        </template>
      </nut-tabbar-item>
      <nut-tabbar-item tab-title="Find">
        <template #icon>
          <Find></Find>
        </template>
      </nut-tabbar-item>
      <nut-tabbar-item tab-title="Cart">
        <template #icon>
          <Cart></Cart>
        </template>
      </nut-tabbar-item>
      <nut-tabbar-item tab-title="My">
        <template #icon>
          <My></My>
        </template>
      </nut-tabbar-item>
    </nut-tabbar>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { Home, Category, Find, Cart, My } from '@nutui/icons-vue'
import router from '@/router'
const active = ref(0)

// 切换 tab 时触发
const tabSwitch = (item: Record<string, unknown>, index: number) => {
  //如果item等于home则路由到home
  if (item.tabTitle === 'Home') {
    router.push('/')
  }else {
    router.push('/about')
  }
}
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh; /* 确保容器高度占满视口 */
}

.content {
  flex: 1; /* 填充剩余空间 */
  overflow: auto; /* 如果内容超出，可以滚动 */
}

.tabbar {
  flex-shrink: 0; /* 固定高度 */
}
</style>
