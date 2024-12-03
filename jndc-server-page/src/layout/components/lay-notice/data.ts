import { ref } from "vue";

export interface ListItem {
  avatar: string;
  title: string;
  datetime: string;
  type: string;
  description: string;
  status?: "primary" | "success" | "warning" | "info" | "danger";
  extra?: string;
}

export interface TabItem {
  key: string;
  name: string;
  list: ListItem[];
  emptyText: string;
}

export const noticesData: TabItem[] = [
  {
    key: "1",
    name: "通知",
    list: [],
    emptyText: "暂无通知"
  },
  {
    key: "2",
    name: "消息",
    list: [],
    emptyText: "暂无消息"
  },
  {
    key: "3",
    name: "待办",
    list: [],
    emptyText: "暂无待办"
  }
];
export const noticesNum = ref(0);
export const notices = ref(noticesData);

export const addNotice = (item: ListItem) => {
  notices.value[0].list.unshift(item);
  noticesNum.value++;
};

export const addMessage = (item: ListItem) => {
  notices.value[1].list.unshift(item);
};

export const addTodo = (item: ListItem) => {
  notices.value[2].list.unshift(item);
};
