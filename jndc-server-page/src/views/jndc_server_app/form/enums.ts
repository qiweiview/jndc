const serverAppStatus: Array<any> = [
  {
    label: "暂停",
    value: "STOP",
    labelType: "warning",
    optional: true
  },
  {
    label: "监听",
    value: "LISTEN",
    labelType: "success",
    optional: true
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

export { serverAppStatus, getLabelByValue, getLabelTypeByValue };
