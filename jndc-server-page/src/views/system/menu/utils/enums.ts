import type { OptionsType } from "@/components/ReSegmented";

const menuTypeOptions: Array<OptionsType> = [
  {
    label: "目录",
    value: 0,
    icon: "noto:crying-face",
    disabled: false
  },
  {
    label: "菜单",
    value: 1,
    icon: "noto:face-savoring-food",
    disabled: false
  },
  {
    label: "iframe",
    value: 2,
    icon: "noto:face-with-steam-from-nose",
    disabled: false
  },
  {
    label: "外链",
    value: 3,
    icon: "noto:grinning-face-with-sweat",
    disabled: false
  },
  {
    label: "按钮",
    value: 4,
    icon: "noto:grinning-squinting-face",
    disabled: false
  }
];

const visibleOptions: Array<OptionsType> = [
  {
    label: "显示",
    tip: "会在菜单中显示",
    value: 0
  },
  {
    label: "隐藏",
    tip: "不会在菜单中显示",
    value: 1
  }
];

const keepAliveOptions: Array<OptionsType> = [
  {
    label: "不缓存",
    tip: "会保存该页面的整体状态，刷新后会清空状态",
    value: 0
  },
  {
    label: "缓存",
    tip: "不会保存该页面的整体状态",
    value: 1
  }
];

const frameLoadingOptions: Array<OptionsType> = [
  {
    label: "关闭",
    tip: "无首次加载动画",
    value: 0
  },
  {
    label: "开启",
    tip: "有首次加载动画",
    value: 1
  }
];

export {
  menuTypeOptions,
  visibleOptions,
  keepAliveOptions,
  frameLoadingOptions
};
