// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/homepage/index.vue'; // 假设有一个 Home 组件


const routes = [
    {
        path: '/',
        name: 'HomePage',
        component: HomePage,
    }
];

const router = createRouter({
    history: createWebHistory(), // 使用 HTML5 模式的路由
    routes,
});

export default router;
