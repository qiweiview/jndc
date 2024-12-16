interface FormItemProps {
  /** 菜单类型（0代表目录、1代表菜单、2代表iframe、3代表外链、4代表按钮）*/
  type: number;
  higherMenuOptions: Record<string, unknown>[];
  parentId: number;
  parentIdString: string;
  title: string;
  name: string;
  path: string;
  component: string;
  sortOrder: number;
  redirect: string;
  icon: string;
  extraIcon: string;
  enterTransition: string;
  leaveTransition: string;
  activePath: string;
  perms: string;
  frameSrc: string;
  frameLoading: number;
  cacheFlag: number;
  hiddenTag: boolean;
  fixedTag: boolean;
  visible: number;
  showParent: boolean;
}
interface FormProps {
  formInline: FormItemProps;
}
export const getMenuType = (type: number, text = false): any => {
  switch (type) {
    case 0:
      return text ? "目录" : "success";
    case 1:
      return text ? "菜单" : "primary";
    case 2:
      return text ? "iframe" : "warning";
    case 3:
      return text ? "外链" : "danger";
    case 4:
      return text ? "按钮" : "info";
    default:
      return "";
  }
};
export type { FormItemProps, FormProps };
