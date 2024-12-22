const jndcServerStatus: Array<any> = [
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

function getLabelByValue(value: string): string {
  const item = jndcServerStatus.find(item => item.value === value);
  const label = item ? item.label : "";
  return label;
}

function getLabelTypeByValue(value: string): string {
  const item = jndcServerStatus.find(item => item.value === value);
  const labelType = item ? item.labelType : "";
  return labelType;
}

export { jndcServerStatus, getLabelByValue, getLabelTypeByValue };
