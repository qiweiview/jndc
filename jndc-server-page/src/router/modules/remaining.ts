const Layout = () => import("@/layout/index.vue");

export default [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/login/index.vue"),
    meta: {
      title: "登录",
      showLink: false,
      sortOrder: 101
    }
  },
  {
    path: "/redirect",
    component: Layout,
    meta: {
      title: "加载中...",
      showLink: false,
      sortOrder: 102
    },
    children: [
      {
        path: "/redirect/:path(.*)",
        name: "Redirect",
        component: () => import("@/layout/redirect.vue")
      }
    ]
  },
  {
    path: "/account-settings",
    name: "AccountSettings",
    component: () => import("@/views/user-account/index.vue"),
    meta: {
      title: "个人中心",
      showLink: false
    }
  }
] satisfies Array<RouteConfigsTable>;
