// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/home.vue'; // 假设有一个 Home 组件
import About from '@/views/about.vue'; // 假设有一个 About 组件

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
    },
    {
        path: '/about',
        name: 'About',
        component: About,
    },
];

const router = createRouter({
    history: createWebHistory(), // 使用 HTML5 模式的路由
    routes,
});

export default router;
