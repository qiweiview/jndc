// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import NutUIResolver from '@nutui/auto-import-resolver'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 开启 unplugin 插件，自动引入 NutUI 组件
    Components({
      resolvers: [NutUIResolver()],
    }),
  ],
  resolve: {
    alias: {
        // 设置 @ 指向 src 目录
      '@': path.resolve(__dirname, 'src'), // 将 '@' 映射到 src 目录
    },
  }
})
