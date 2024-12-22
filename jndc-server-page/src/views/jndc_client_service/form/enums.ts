import type { OptionsType } from "@/components/ReSegmented";

//组建原因值倒置了
const autoRegisterOption: Array<OptionsType> = [
  {
    label: "自动注册",
    tip: "会在客户端启动后自动注册",
    value: 0
  },
  {
    label: "手动注册",
    tip: "需要手动注册",
    value: 1
  }
];

const jndcClientServiceStatus: Array<any> = [
  {
    label: "注册",
    value: "register",
    optional: true,
    labelType: "success"
  },
  {
    label: "未注册",
    value: "unregister",
    optional: true,
    labelType: "warning"
  }
];

function getLabelByValue(value: string): string {
  const item = jndcClientServiceStatus.find(item => item.value === value);
  const label = item ? item.label : "";
  return label;
}

function getLabelTypeByValue(value: string): string {
  const item = jndcClientServiceStatus.find(item => item.value === value);
  const labelType = item ? item.labelType : "";
  return labelType;
}

export {
  autoRegisterOption,
  jndcClientServiceStatus,
  getLabelByValue,
  getLabelTypeByValue
};
