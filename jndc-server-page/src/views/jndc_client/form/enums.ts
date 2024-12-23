const jndcClientStatus: Array<any> = [
  {
    label: "连接",
    value: "connect",
    exist: true,
    chooseAble: false,
    labelType: "success"
  },
  {
    label: "断开",
    value: "pause",
    exist: true,
    chooseAble: false,
    labelType: "warning"
  },
  {
    label: "处理中",
    value: "processing",
    exist: true,
    chooseAble: true,
    labelType: "primary"
  }
];

const jndcClientStatusCondition = (currentStatus: string): Array<any> => {
  if (currentStatus == "processing") {
    //拷贝jndcClientStatus并只保留处理中
    return jndcClientStatus.filter(item => item.value === "processing");
  } else if (currentStatus == "pause" || currentStatus == "connect") {
    //拷贝jndcClientStatus并只保留断开和连接
    return jndcClientStatus.filter(
      item => item.value === "pause" || item.value === "connect"
    );
  }
};

const autoReConnect: Array<any> = [
  {
    label: "是",
    value: 1,
    optional: true,
    labelType: "success"
  },
  {
    label: "否",
    value: 0,
    optional: true,
    labelType: "warning"
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

export {
  jndcClientStatus,
  autoReConnect,
  jndcClientStatusCondition,
  getLabelByValue,
  getLabelTypeByValue
};
