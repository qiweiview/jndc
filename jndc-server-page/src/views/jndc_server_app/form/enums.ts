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

const contentTypes: Array<any> = [
  {
    label: "application/json",
    value: "application/json"
  },
  {
    label: "application/x-www-form-urlencoded",
    value: "application/x-www-form-urlencoded"
  },
  {
    label: "application/xml",
    value: "application/xml"
  },
  {
    label: "text/plain",
    value: "text/plain"
  },
  {
    label: "text/html",
    value: "text/html"
  },
  {
    label: "text/xml",
    value: "text/xml"
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

export {
  serverAppStatus,
  serverAppType,
  contentTypes,
  getLabelByValue,
  getLabelTypeByValue
};
