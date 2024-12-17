import { defineStore } from "pinia";
import { storageSession } from "@pureadmin/utils";
import { getAllDictAndData } from "@/api/system/dict/dict";
import { store } from "../utils";

export type dictType = {
  dictCode: string;
  dictValue: dictDataType[];
};

export type dictDataType = {
  name: string;
  value: any;
  color: string;
};

const DICT_CACHE_KEY = "halcyon-dict";

export const useDictStore = defineStore({
  id: "halcyon-dict",
  state: () => {
    return {
      dictList: storageSession().getItem(DICT_CACHE_KEY) as dictType[]
    };
  },
  actions: {
    // 从后端请求所有字典数据
    async fetchAllDictsFromServer(): Promise<void> {
      try {
        const response = await getAllDictAndData(); // 请求所有字典数据的API
        const allDicts = response.data as dictType[];
        this.setDictList(allDicts);
      } catch (error) {
        console.error("字典数据获取失败:", error);
      }
    },

    // 加载字典数据（优先从 sessionStorage 中获取）
    async loadDicts(): Promise<void> {
      const storedDictList = storageSession().getItem(DICT_CACHE_KEY);

      if (storedDictList) {
        this.dictList = storedDictList;
      } else {
        await this.fetchAllDictsFromServer();
      }
    },

    // 根据 dictCode 获取字典数据
    getDictByCode(dictCode: string): dictDataType[] | undefined {
      const dict = this.dictList.find(d => d.dictCode === dictCode);
      return dict ? dict.dictValue : undefined;
    },

    // 设置字典列表并存储到 sessionStorage
    setDictList(dictList: dictType[]) {
      this.dictList = dictList;
      storageSession().setItem(DICT_CACHE_KEY, dictList);
    },

    // 清除字典数据
    clearDict() {
      this.dictList = [];
      storageSession().removeItem(DICT_CACHE_KEY);
    }
  }
});

// 针对外部调用的钩子函数
export function useDictStoreHook() {
  return useDictStore(store);
}
