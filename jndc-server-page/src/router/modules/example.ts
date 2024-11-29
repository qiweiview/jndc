import {example} from "@/router/enums";

export default {
  path: "/example",
  redirect: "/example/table",
  meta: {
    icon: "ri:information-line",
    // showLink: false,
    title: "范例",
    rank: example
  },
  children: [
    {
      path: "/example/table",
      name: "table",
      component: () => import("@/views/example/table/index.vue"),
      meta: {
        title: "表格"
      }
    },
    {
      path: "/example/form",
      name: "form",
      component: () => import("@/views/example/form/dialog.vue"),
      meta: {
        title: "表单"
      }
    }
  ]
} satisfies RouteConfigsTable;
