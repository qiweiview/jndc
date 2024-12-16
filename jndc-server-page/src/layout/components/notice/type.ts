export interface ListItem {
  title: string;
  createTime: string;
  type: number;
  id: number;
  content: string;
  creator: string;
  readStatus: number;
}

export interface TabItem {
  key: string;
  name: string;
  list: ListItem[];
  total: number;
}

export const noticesType: TabItem[] = [
  {
    key: "1",
    name: "通知",
    list: [],
    total: 0
  },
  {
    key: "2",
    name: "公告",
    list: [],
    total: 0
  }
];
