import { admin } from "@/router/enums";

export default {
  path: "/admin",
  redirect: "/admin/pure-user",
  meta: {
    icon: "ri:information-line",
    // showLink: false,
    title: "管理",
    rank: admin
  },
  children: [
    {
      path: "/admin/pure-user",
      name: "pure-user",
      component: () => import("@/views/admin/index.vue"),
      meta: {
        title: "用户"
      }
    },
    {
      path: "/admin/pure-user2",
      name: "pure-user2",
      component: () => import("@/views/admin/index2.vue"),
      meta: {
        title: "用户2"
      }
    }
  ]
} satisfies RouteConfigsTable;
