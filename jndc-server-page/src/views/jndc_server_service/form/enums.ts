const serverserviceStatus: Array<any> = [
  {
    label: "离线",
    value: "unregister",
    labelType: "warning",
    optional: true
  },
  {
    label: "在线",
    value: "register",
    labelType: "success",
    optional: true
  }
];

function getLabelByValue(value: string): string {
  const item = serverserviceStatus.find(item => item.value === value);
  const label = item ? item.label : "";
  return label;
}

function getLabelTypeByValue(value: string): string {
  const item = serverserviceStatus.find(item => item.value === value);
  const labelType = item ? item.labelType : "";
  return labelType;
}

export { getLabelByValue, getLabelTypeByValue };
