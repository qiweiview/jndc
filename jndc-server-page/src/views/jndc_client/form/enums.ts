const jndcClientStatus: Array<any> = [
  {
    label: "断开",
    value: "pause",
    optional: true,
    labelType: "warning"
  },
  {
    label: "连接",
    value: "connect",
    optional: true,
    labelType: "success"
  },
  {
    label: "处理中",
    value: "processing",
    optional: false,
    labelType: "primary"
  }
];

function getLabelByValue(value: string): string {
  const item = jndcClientStatus.find(item => item.value === value);
  const label = item ? item.label : "";
  return label;
}

function getLabelTypeByValue(value: string): string {
  const item = jndcClientStatus.find(item => item.value === value);
  const labelType = item ? item.labelType : "";
  return labelType;
}

export { jndcClientStatus, getLabelByValue, getLabelTypeByValue };
