import axios from "axios";
import type { App } from "vue";
import { storageLocal } from "@pureadmin/utils";

let config: object = {};
const { VITE_PUBLIC_PATH } = import.meta.env;
const backstageKey = "backstagePlatformConfig";
const setConfig = (cfg?: unknown) => {
  config = Object.assign(config, cfg);
};

const getConfig = (key?: string): PlatformConfigs => {
  if (typeof key === "string") {
    const arr = key.split(".");
    if (arr && arr.length) {
      let data = config;
      arr.forEach(v => {
        if (data && typeof data[v] !== "undefined") {
          data = data[v];
        } else {
          data = null;
        }
      });
      return data;
    }
  }
  return config;
};

/** 获取项目动态全局配置 */
export const getPlatformConfig = async (app: App): Promise<undefined> => {
  app.config.globalProperties.$config = getConfig();
  const backstageConfig = getBackstageConfig();

  if (backstageConfig) {
    app.config.globalProperties.$config = backstageConfig;
    setConfig(backstageConfig);
    return backstageConfig as undefined;
  }
  // return;
  // return axios({
  //   method: "get",
  //   url: `${VITE_PUBLIC_PATH}platform-config.json`
  // })
  //   .then(({ data: config }) => {
  //     console.log("config", config);
  //     let $config = app.config.globalProperties.$config;
  //     // 自动注入系统配置
  //     if (app && $config && typeof config === "object") {
  //       $config = Object.assign($config, config);
  //       app.config.globalProperties.$config = $config;
  //       // 设置全局配置
  //       setConfig($config);
  //     }
  //     return $config;
  //   })
  //   .catch(() => {
  //     throw "请在public文件夹下添加platform-config.json配置文件";
  //   });

  // 请求不到后端则从本地获取
  return axios({
    method: "get",
    url: `${VITE_PUBLIC_PATH}platform-config.json`
  })
    .then(({ data: config }) => {
      let $config = app.config.globalProperties.$config;
      // 自动注入系统配置
      if (app && $config && typeof config === "object") {
        $config = Object.assign($config, config);
        app.config.globalProperties.$config = $config;
        // 设置全局配置
        setConfig($config);
      }
      return $config;
    })
    .catch(() => {
      throw "请在public文件夹下添加platform-config.json配置文件";
    });
};

const getBackstageConfig = () => {
  return storageLocal().getItem(backstageKey);
};
// 移除后台配置，向后端请求重新载入后台配置
const removeBackstageConfig = () => {
  storageLocal().removeItem(backstageKey);
};
/** 本地响应式存储的命名空间 */
const responsiveStorageNameSpace = () => getConfig().ResponsiveStorageNameSpace;

export {
  getConfig,
  setConfig,
  responsiveStorageNameSpace,
  removeBackstageConfig
};
