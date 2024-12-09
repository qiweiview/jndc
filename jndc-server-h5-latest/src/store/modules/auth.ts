// stores/auth.js
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: null
    // 可以添加其他用户信息
  }),
  actions: {
    setToken(token) {
      this.token = token;
      // 将 token 保存在本地存储中，以便在页面刷新后仍然保留登录状态
      localStorage.setItem("token", token);
    },
    logout() {
      this.token = null;
      localStorage.removeItem("token");
    }
    // 可以添加其他与认证相关的操作
  }
});
