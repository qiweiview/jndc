// vite.config.ts
import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/vite@5.4.10_@types+node@22.8.7_less@4.2.0_terser@5.36.0/node_modules/vite/dist/node/index.js";
import vue from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/@vitejs+plugin-vue@5.1.4_vite@5.4.10_@types+node@22.8.7_less@4.2.0_terser@5.36.0__vue@3.5.12_typescript@5.6.3_/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import vueJsx from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/@vitejs+plugin-vue-jsx@4.0.1_vite@5.4.10_@types+node@22.8.7_less@4.2.0_terser@5.36.0__vue@3.5.12_typescript@5.6.3_/node_modules/@vitejs/plugin-vue-jsx/dist/index.mjs";
import Components from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/unplugin-vue-components@0.27.4_@babel+parser@7.26.2_rollup@4.24.3_vue@3.5.12_typescript@5.6.3__webpack-sources@3.2.3/node_modules/unplugin-vue-components/dist/vite.js";
import { VantResolver } from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/unplugin-vue-components@0.27.4_@babel+parser@7.26.2_rollup@4.24.3_vue@3.5.12_typescript@5.6.3__webpack-sources@3.2.3/node_modules/unplugin-vue-components/dist/resolvers.js";
import { createSvgIconsPlugin } from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/vite-plugin-svg-icons@2.0.1_vite@5.4.10_@types+node@22.8.7_less@4.2.0_terser@5.36.0_/node_modules/vite-plugin-svg-icons/dist/index.mjs";
import path from "path";
import mockDevServerPlugin from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/vite-plugin-mock-dev-server@1.8.0_esbuild@0.21.5_rollup@4.24.3_vite@5.4.10_@types+node@22.8.7_less@4.2.0_terser@5.36.0_/node_modules/vite-plugin-mock-dev-server/dist/index.js";
import viteCompression from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/vite-plugin-compression@0.5.1_vite@5.4.10_@types+node@22.8.7_less@4.2.0_terser@5.36.0_/node_modules/vite-plugin-compression/dist/index.mjs";
import { createHtmlPlugin } from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/vite-plugin-html@3.2.2_vite@5.4.10_@types+node@22.8.7_less@4.2.0_terser@5.36.0_/node_modules/vite-plugin-html/dist/index.mjs";

// build/cdn.ts
import { cdn } from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/vite-plugin-cdn2@1.1.0_rollup@4.24.3/node_modules/vite-plugin-cdn2/dist/index.mjs";
import { unpkg } from "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/node_modules/.pnpm/vite-plugin-cdn2@1.1.0_rollup@4.24.3/node_modules/vite-plugin-cdn2/dist/resolver/unpkg.mjs";
function enableCDN(isEnabled) {
  if (isEnabled === "true") {
    return cdn({
      resolve: unpkg(),
      modules: ["vue", "vue-demi", "pinia", "axios", "vant", "vue-router"]
    });
  }
}

// vite.config.ts
var __vite_injected_original_import_meta_url = "file:///D:/JAVA_WORK_SPACE/jndc/jndc-server-h5-latest/vite.config.ts";
var root = process.cwd();
var vite_config_default = defineConfig(({ mode }) => {
  const env = loadEnv(mode, root, "");
  return {
    base: env.VITE_PUBLIC_PATH || "/",
    plugins: [
      vue(),
      vueJsx(),
      mockDevServerPlugin(),
      // vant 组件自动按需引入
      Components({
        dts: "src/typings/components.d.ts",
        resolvers: [VantResolver()]
      }),
      // svg icon
      createSvgIconsPlugin({
        // 指定图标文件夹
        iconDirs: [path.resolve(root, "src/icons/svg")],
        // 指定 symbolId 格式
        symbolId: "icon-[dir]-[name]"
      }),
      // 生产环境 gzip 压缩资源
      viteCompression(),
      // 注入模板数据
      createHtmlPlugin({
        inject: {
          data: {
            ENABLE_ERUDA: env.VITE_ENABLE_ERUDA || "false"
          }
        }
      }),
      // 生产环境默认不启用 CDN 加速
      enableCDN(env.VITE_CDN_DEPS)
    ],
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", __vite_injected_original_import_meta_url))
      }
    },
    server: {
      host: true,
      // 仅在 proxy 中配置的代理前缀， mock-dev-server 才会拦截并 mock
      // doc: https://github.com/pengzhanbo/vite-plugin-mock-dev-server
      proxy: {
        "^/dev-api": {
          target: ""
        }
      }
    },
    build: {
      rollupOptions: {
        output: {
          chunkFileNames: "static/js/[name]-[hash].js",
          entryFileNames: "static/js/[name]-[hash].js",
          assetFileNames: "static/[ext]/[name]-[hash].[ext]"
        }
      }
    }
  };
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiLCAiYnVpbGQvY2RuLnRzIl0sCiAgInNvdXJjZXNDb250ZW50IjogWyJjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZGlybmFtZSA9IFwiRDpcXFxcSkFWQV9XT1JLX1NQQUNFXFxcXGpuZGNcXFxcam5kYy1zZXJ2ZXItaDUtbGF0ZXN0XCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCJEOlxcXFxKQVZBX1dPUktfU1BBQ0VcXFxcam5kY1xcXFxqbmRjLXNlcnZlci1oNS1sYXRlc3RcXFxcdml0ZS5jb25maWcudHNcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfaW1wb3J0X21ldGFfdXJsID0gXCJmaWxlOi8vL0Q6L0pBVkFfV09SS19TUEFDRS9qbmRjL2puZGMtc2VydmVyLWg1LWxhdGVzdC92aXRlLmNvbmZpZy50c1wiO2ltcG9ydCB7IGZpbGVVUkxUb1BhdGgsIFVSTCB9IGZyb20gXCJub2RlOnVybFwiO1xuaW1wb3J0IHsgZGVmaW5lQ29uZmlnLCBsb2FkRW52IH0gZnJvbSBcInZpdGVcIjtcbmltcG9ydCB2dWUgZnJvbSBcIkB2aXRlanMvcGx1Z2luLXZ1ZVwiO1xuaW1wb3J0IHZ1ZUpzeCBmcm9tIFwiQHZpdGVqcy9wbHVnaW4tdnVlLWpzeFwiO1xuaW1wb3J0IENvbXBvbmVudHMgZnJvbSBcInVucGx1Z2luLXZ1ZS1jb21wb25lbnRzL3ZpdGVcIjtcbmltcG9ydCB7IFZhbnRSZXNvbHZlciB9IGZyb20gXCJ1bnBsdWdpbi12dWUtY29tcG9uZW50cy9yZXNvbHZlcnNcIjtcbmltcG9ydCB7IGNyZWF0ZVN2Z0ljb25zUGx1Z2luIH0gZnJvbSBcInZpdGUtcGx1Z2luLXN2Zy1pY29uc1wiO1xuaW1wb3J0IHBhdGggZnJvbSBcInBhdGhcIjtcbmltcG9ydCBtb2NrRGV2U2VydmVyUGx1Z2luIGZyb20gXCJ2aXRlLXBsdWdpbi1tb2NrLWRldi1zZXJ2ZXJcIjtcbmltcG9ydCB2aXRlQ29tcHJlc3Npb24gZnJvbSBcInZpdGUtcGx1Z2luLWNvbXByZXNzaW9uXCI7XG5pbXBvcnQgeyBjcmVhdGVIdG1sUGx1Z2luIH0gZnJvbSBcInZpdGUtcGx1Z2luLWh0bWxcIjtcbmltcG9ydCB7IGVuYWJsZUNETiB9IGZyb20gXCIuL2J1aWxkL2NkblwiO1xuaW1wb3J0IHsgVERlc2lnblJlc29sdmVyIH0gZnJvbSAndW5wbHVnaW4tdnVlLWNvbXBvbmVudHMvcmVzb2x2ZXJzJztcblxuLy8gXHU1RjUzXHU1MjREXHU1REU1XHU0RjVDXHU3NkVFXHU1RjU1XHU4REVGXHU1Rjg0XG5jb25zdCByb290OiBzdHJpbmcgPSBwcm9jZXNzLmN3ZCgpO1xuXG4vLyBodHRwczovL3ZpdGVqcy5kZXYvY29uZmlnL1xuZXhwb3J0IGRlZmF1bHQgZGVmaW5lQ29uZmlnKCh7IG1vZGUgfSkgPT4ge1xuICAvLyBcdTczQUZcdTU4ODNcdTUzRDhcdTkxQ0ZcbiAgY29uc3QgZW52ID0gbG9hZEVudihtb2RlLCByb290LCBcIlwiKTtcbiAgcmV0dXJuIHtcbiAgICBiYXNlOiBlbnYuVklURV9QVUJMSUNfUEFUSCB8fCBcIi9cIixcbiAgICBwbHVnaW5zOiBbXG4gICAgICB2dWUoKSxcbiAgICAgIHZ1ZUpzeCgpLFxuICAgICAgbW9ja0RldlNlcnZlclBsdWdpbigpLFxuICAgICAgLy8gdmFudCBcdTdFQzRcdTRFRjZcdTgxRUFcdTUyQThcdTYzMDlcdTk3MDBcdTVGMTVcdTUxNjVcbiAgICAgIENvbXBvbmVudHMoe1xuICAgICAgICBkdHM6IFwic3JjL3R5cGluZ3MvY29tcG9uZW50cy5kLnRzXCIsXG4gICAgICAgIHJlc29sdmVyczogW1ZhbnRSZXNvbHZlcigpXVxuICAgICAgfSksXG4gICAgICAvLyBzdmcgaWNvblxuICAgICAgY3JlYXRlU3ZnSWNvbnNQbHVnaW4oe1xuICAgICAgICAvLyBcdTYzMDdcdTVCOUFcdTU2RkVcdTY4MDdcdTY1ODdcdTRFRjZcdTU5MzlcbiAgICAgICAgaWNvbkRpcnM6IFtwYXRoLnJlc29sdmUocm9vdCwgXCJzcmMvaWNvbnMvc3ZnXCIpXSxcbiAgICAgICAgLy8gXHU2MzA3XHU1QjlBIHN5bWJvbElkIFx1NjgzQ1x1NUYwRlxuICAgICAgICBzeW1ib2xJZDogXCJpY29uLVtkaXJdLVtuYW1lXVwiXG4gICAgICB9KSxcbiAgICAgIC8vIFx1NzUxRlx1NEVBN1x1NzNBRlx1NTg4MyBnemlwIFx1NTM4Qlx1N0YyOVx1OEQ0NFx1NkU5MFxuICAgICAgdml0ZUNvbXByZXNzaW9uKCksXG4gICAgICAvLyBcdTZDRThcdTUxNjVcdTZBMjFcdTY3N0ZcdTY1NzBcdTYzNkVcbiAgICAgIGNyZWF0ZUh0bWxQbHVnaW4oe1xuICAgICAgICBpbmplY3Q6IHtcbiAgICAgICAgICBkYXRhOiB7XG4gICAgICAgICAgICBFTkFCTEVfRVJVREE6IGVudi5WSVRFX0VOQUJMRV9FUlVEQSB8fCBcImZhbHNlXCJcbiAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgIH0pLFxuICAgICAgLy8gXHU3NTFGXHU0RUE3XHU3M0FGXHU1ODgzXHU5RUQ4XHU4QkE0XHU0RTBEXHU1NDJGXHU3NTI4IENETiBcdTUyQTBcdTkwMUZcbiAgICAgIGVuYWJsZUNETihlbnYuVklURV9DRE5fREVQUylcbiAgICBdLFxuICAgIHJlc29sdmU6IHtcbiAgICAgIGFsaWFzOiB7XG4gICAgICAgIFwiQFwiOiBmaWxlVVJMVG9QYXRoKG5ldyBVUkwoXCIuL3NyY1wiLCBpbXBvcnQubWV0YS51cmwpKVxuICAgICAgfVxuICAgIH0sXG4gICAgc2VydmVyOiB7XG4gICAgICBob3N0OiB0cnVlLFxuICAgICAgLy8gXHU0RUM1XHU1NzI4IHByb3h5IFx1NEUyRFx1OTE0RFx1N0Y2RVx1NzY4NFx1NEVFM1x1NzQwNlx1NTI0RFx1N0YwMFx1RkYwQyBtb2NrLWRldi1zZXJ2ZXIgXHU2MjREXHU0RjFBXHU2MkU2XHU2MjJBXHU1RTc2IG1vY2tcbiAgICAgIC8vIGRvYzogaHR0cHM6Ly9naXRodWIuY29tL3Blbmd6aGFuYm8vdml0ZS1wbHVnaW4tbW9jay1kZXYtc2VydmVyXG4gICAgICBwcm94eToge1xuICAgICAgICBcIl4vZGV2LWFwaVwiOiB7XG4gICAgICAgICAgdGFyZ2V0OiBcIlwiXG4gICAgICAgIH1cbiAgICAgIH1cbiAgICB9LFxuICAgIGJ1aWxkOiB7XG4gICAgICByb2xsdXBPcHRpb25zOiB7XG4gICAgICAgIG91dHB1dDoge1xuICAgICAgICAgIGNodW5rRmlsZU5hbWVzOiBcInN0YXRpYy9qcy9bbmFtZV0tW2hhc2hdLmpzXCIsXG4gICAgICAgICAgZW50cnlGaWxlTmFtZXM6IFwic3RhdGljL2pzL1tuYW1lXS1baGFzaF0uanNcIixcbiAgICAgICAgICBhc3NldEZpbGVOYW1lczogXCJzdGF0aWMvW2V4dF0vW25hbWVdLVtoYXNoXS5bZXh0XVwiXG4gICAgICAgIH1cbiAgICAgIH1cbiAgICB9XG4gIH07XG59KTtcbiIsICJjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZGlybmFtZSA9IFwiRDpcXFxcSkFWQV9XT1JLX1NQQUNFXFxcXGpuZGNcXFxcam5kYy1zZXJ2ZXItaDUtbGF0ZXN0XFxcXGJ1aWxkXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCJEOlxcXFxKQVZBX1dPUktfU1BBQ0VcXFxcam5kY1xcXFxqbmRjLXNlcnZlci1oNS1sYXRlc3RcXFxcYnVpbGRcXFxcY2RuLnRzXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ltcG9ydF9tZXRhX3VybCA9IFwiZmlsZTovLy9EOi9KQVZBX1dPUktfU1BBQ0Uvam5kYy9qbmRjLXNlcnZlci1oNS1sYXRlc3QvYnVpbGQvY2RuLnRzXCI7aW1wb3J0IHsgY2RuIH0gZnJvbSBcInZpdGUtcGx1Z2luLWNkbjJcIjtcbmltcG9ydCB7IHVucGtnIH0gZnJvbSBcInZpdGUtcGx1Z2luLWNkbjIvcmVzb2x2ZXIvdW5wa2dcIjtcblxuZXhwb3J0IGZ1bmN0aW9uIGVuYWJsZUNETihpc0VuYWJsZWQ6IHN0cmluZykge1xuICBpZiAoaXNFbmFibGVkID09PSBcInRydWVcIikge1xuICAgIHJldHVybiBjZG4oe1xuICAgICAgcmVzb2x2ZTogdW5wa2coKSxcbiAgICAgIG1vZHVsZXM6IFtcInZ1ZVwiLCBcInZ1ZS1kZW1pXCIsIFwicGluaWFcIiwgXCJheGlvc1wiLCBcInZhbnRcIiwgXCJ2dWUtcm91dGVyXCJdXG4gICAgfSk7XG4gIH1cbn1cbiJdLAogICJtYXBwaW5ncyI6ICI7QUFBaVUsU0FBUyxlQUFlLFdBQVc7QUFDcFcsU0FBUyxjQUFjLGVBQWU7QUFDdEMsT0FBTyxTQUFTO0FBQ2hCLE9BQU8sWUFBWTtBQUNuQixPQUFPLGdCQUFnQjtBQUN2QixTQUFTLG9CQUFvQjtBQUM3QixTQUFTLDRCQUE0QjtBQUNyQyxPQUFPLFVBQVU7QUFDakIsT0FBTyx5QkFBeUI7QUFDaEMsT0FBTyxxQkFBcUI7QUFDNUIsU0FBUyx3QkFBd0I7OztBQ1ZvUyxTQUFTLFdBQVc7QUFDelYsU0FBUyxhQUFhO0FBRWYsU0FBUyxVQUFVLFdBQW1CO0FBQzNDLE1BQUksY0FBYyxRQUFRO0FBQ3hCLFdBQU8sSUFBSTtBQUFBLE1BQ1QsU0FBUyxNQUFNO0FBQUEsTUFDZixTQUFTLENBQUMsT0FBTyxZQUFZLFNBQVMsU0FBUyxRQUFRLFlBQVk7QUFBQSxJQUNyRSxDQUFDO0FBQUEsRUFDSDtBQUNGOzs7QURWeU0sSUFBTSwyQ0FBMkM7QUFlMVAsSUFBTSxPQUFlLFFBQVEsSUFBSTtBQUdqQyxJQUFPLHNCQUFRLGFBQWEsQ0FBQyxFQUFFLEtBQUssTUFBTTtBQUV4QyxRQUFNLE1BQU0sUUFBUSxNQUFNLE1BQU0sRUFBRTtBQUNsQyxTQUFPO0FBQUEsSUFDTCxNQUFNLElBQUksb0JBQW9CO0FBQUEsSUFDOUIsU0FBUztBQUFBLE1BQ1AsSUFBSTtBQUFBLE1BQ0osT0FBTztBQUFBLE1BQ1Asb0JBQW9CO0FBQUE7QUFBQSxNQUVwQixXQUFXO0FBQUEsUUFDVCxLQUFLO0FBQUEsUUFDTCxXQUFXLENBQUMsYUFBYSxDQUFDO0FBQUEsTUFDNUIsQ0FBQztBQUFBO0FBQUEsTUFFRCxxQkFBcUI7QUFBQTtBQUFBLFFBRW5CLFVBQVUsQ0FBQyxLQUFLLFFBQVEsTUFBTSxlQUFlLENBQUM7QUFBQTtBQUFBLFFBRTlDLFVBQVU7QUFBQSxNQUNaLENBQUM7QUFBQTtBQUFBLE1BRUQsZ0JBQWdCO0FBQUE7QUFBQSxNQUVoQixpQkFBaUI7QUFBQSxRQUNmLFFBQVE7QUFBQSxVQUNOLE1BQU07QUFBQSxZQUNKLGNBQWMsSUFBSSxxQkFBcUI7QUFBQSxVQUN6QztBQUFBLFFBQ0Y7QUFBQSxNQUNGLENBQUM7QUFBQTtBQUFBLE1BRUQsVUFBVSxJQUFJLGFBQWE7QUFBQSxJQUM3QjtBQUFBLElBQ0EsU0FBUztBQUFBLE1BQ1AsT0FBTztBQUFBLFFBQ0wsS0FBSyxjQUFjLElBQUksSUFBSSxTQUFTLHdDQUFlLENBQUM7QUFBQSxNQUN0RDtBQUFBLElBQ0Y7QUFBQSxJQUNBLFFBQVE7QUFBQSxNQUNOLE1BQU07QUFBQTtBQUFBO0FBQUEsTUFHTixPQUFPO0FBQUEsUUFDTCxhQUFhO0FBQUEsVUFDWCxRQUFRO0FBQUEsUUFDVjtBQUFBLE1BQ0Y7QUFBQSxJQUNGO0FBQUEsSUFDQSxPQUFPO0FBQUEsTUFDTCxlQUFlO0FBQUEsUUFDYixRQUFRO0FBQUEsVUFDTixnQkFBZ0I7QUFBQSxVQUNoQixnQkFBZ0I7QUFBQSxVQUNoQixnQkFBZ0I7QUFBQSxRQUNsQjtBQUFBLE1BQ0Y7QUFBQSxJQUNGO0FBQUEsRUFDRjtBQUNGLENBQUM7IiwKICAibmFtZXMiOiBbXQp9Cg==
