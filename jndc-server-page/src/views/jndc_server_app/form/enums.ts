const serverAppStatus: Array<any> = [
  {
    label: "暂停",
    value: "pause",
    labelType: "warning",
    optional: true
  },
  {
    label: "处理中",
    value: "processing",
    labelType: "primary",
    optional: false
  },
  {
    label: "监听",
    value: "listen",
    labelType: "success",
    optional: true
  }
];

const serverAppType: Array<any> = [
  {
    label: "客户端路由",
    value: "jndc-client"
  },
  {
    label: "Mock服务",
    value: "mock-server"
  }
];

function getLabelByValue(value: string): string {
  const item = serverAppStatus.find(item => item.value === value);
  const label = item ? item.label : "";
  return label;
}

function getLabelTypeByValue(value: string): string {
  const item = serverAppStatus.find(item => item.value === value);
  const labelType = item ? item.labelType : "";
  return labelType;
}

export { serverAppStatus, serverAppType, getLabelByValue, getLabelTypeByValue };
