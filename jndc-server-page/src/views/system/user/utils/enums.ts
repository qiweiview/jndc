const statusOptions: Array<any> = [
  {
    label: "正常",
    value: 0,
    disabled: false
  },
  {
    label: "冻结",
    value: 1,
    disabled: false
  }
];
const genderOptions: Array<any> = [
  {
    label: "未知",
    value: 0,
    disabled: false,
    icon: "noto:unknown-flag"
  },
  {
    label: "男",
    value: 1,
    disabled: false,
    icon: "twemoji:deaf-man-light-skin-tone"
  },
  {
    label: "女",
    value: 2,
    disabled: false,
    icon: "twemoji:deaf-woman-light-skin-tone"
  }
];
export { statusOptions, genderOptions };
