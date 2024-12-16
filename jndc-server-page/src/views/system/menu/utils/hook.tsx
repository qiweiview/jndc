import editForm from "../form/form.vue";
import { handleTree } from "@/utils/tree";
import { message as toast } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { h, onMounted, reactive, ref } from "vue";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { cloneDeep, deviceDetection, isAllEmpty } from "@pureadmin/utils";
import {
  addMenu,
  deleteMenu,
  listMenu,
  updateMenu
} from "@/api/system/menu/menu";
import { menuTypeOptions } from "./enums";
import { getMenuType } from "./types";
import { showDialog } from "@/components/HalcyonDialog";

export function useMenu() {
  const form = reactive({
    title: ""
  });

  const formRef = ref();
  const dataList = ref([]);
  const loading = ref(true);

  const getVisble = (type, text = false) => {
    if (type == 0) {
      return text ? "显示" : "success";
    } else {
      return text ? "隐藏" : "danger";
    }
  };

  const columns: TableColumnList = [
    {
      label: "菜单名称",
      prop: "title",
      align: "left",
      minWidth: 120,
      cellRenderer: ({ row }) => (
        <>
          <span class="inline-block mr-1">
            {h(useRenderIcon(row.icon), {
              style: { paddingTop: "1px" }
            })}
          </span>
          <span>{row.title}</span>
        </>
      )
    },
    {
      label: "id",
      prop: "idString"
    },
    {
      label: "父级id",
      prop: "parentIdString"
    },
    {
      label: "菜单类型",
      prop: "type",
      width: 100,
      cellRenderer: ({ row, props }) => (
        <el-tag size={props.size} type={getMenuType(row.type)} effect="plain">
          {getMenuType(row.type, true)}
        </el-tag>
      )
    },
    {
      label: "路由名称",
      prop: "name"
    },
    {
      label: "路由路径",
      prop: "path"
    },
    {
      label: "组件路径",
      prop: "component"
    },
    {
      label: "权限标识",
      prop: "perms",
      cellRenderer: ({ row }) =>
        row.perms ? (
          <el-text type="success" v-copy:click={row.perms}>
            {row.perms}
          </el-text>
        ) : null
    },
    {
      label: "排序",
      prop: "sortOrder",
      width: 100
    },
    {
      label: "显示状态",
      prop: "visible",
      cellRenderer: ({ row, props }) => (
        <el-tag size={props.size} type={getVisble(row.visible)} effect="plain">
          {getVisble(row.visible, true)}
        </el-tag>
      ),
      width: 100
    },
    {
      label: "操作",
      fixed: "right",
      width: 210,
      slot: "operation"
    }
  ];

  function handleSelectionChange(val) {
    console.log("handleSelectionChange", val);
  }

  function resetForm(formEl) {
    if (!formEl) return;
    formEl.resetFields();
    onSearch();
  }

  async function onSearch() {
    loading.value = true;
    const { code, data } = await listMenu(); // 这里是返回一维数组结构，前端自行处理成树结构，返回格式要求：唯一id加父节点parentId，parentId取父节点id
    let newData = data;
    if (code != 0) {
    } else {
      if (!isAllEmpty(form.title)) {
        // 前端搜索菜单名称
        newData = newData.filter(item => item.title.includes(form.title));
      }
      dataList.value = handleTree(newData); // 处理成树结构
    }
    loading.value = false;
  }

  function formatHigherMenuOptions(treeList) {
    if (!treeList || !treeList.length) return;
    const newTreeList = [];
    for (let i = 0; i < treeList.length; i++) {
      treeList[i].title = treeList[i].title;
      formatHigherMenuOptions(treeList[i].children);
      newTreeList.push(treeList[i]);
    }
    return newTreeList;
  }

  function openDialog(title = "新增", row) {
    menuTypeOptions.forEach(option => {
      option.disabled =
        row?.type === 0 &&
        row?.children &&
        row.children.length > 0 &&
        option.value !== 0;
    });

    addDialog({
      title: `${title}菜单`,
      props: {
        formInline: {
          id: row?.id ?? null,
          idString: row?.idString ?? null,
          type: row?.type ?? 0,
          higherMenuOptions: formatHigherMenuOptions(cloneDeep(dataList.value)),
          parentId: row?.parentId ?? 0,
          parentIdString: row?.parentIdString ?? null,
          title: row?.title ?? "",
          name: row?.name ?? "",
          path: row?.path ?? "",
          component: row?.component ?? "",
          sortOrder: row?.sortOrder ?? 1,
          redirect: row?.redirect ?? "",
          icon: row?.icon ?? "",
          extraIcon: row?.extraIcon ?? "",
          activePath: row?.activePath ?? "",
          perms: row?.perms ?? "",
          frameSrc: row?.frameSrc ?? "",
          frameLoading: row?.frameLoading ?? 0,
          cacheFlag: row?.cacheFlag ?? 0,
          visible: row?.visible ?? 0
        }
      },
      width: "55%",
      top: "2%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef }),
      beforeSure: async (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline;
        function chores() {
          toast(`${title}菜单：${curData.title}成功`, {
            type: "success"
          });
          done(); // 关闭弹框
          onSearch(); // 刷新表格数据
        }
        FormRef.validate(async valid => {
          if (valid) {
            try {
              // 表单规则校验通过
              if (title === "新增") {
                // 实际开发先调用新增接口，再进行下面操作
                const res = await addMenu(curData);
                if (res.code == 0) {
                  chores();
                }
              } else {
                // 实际开发先调用新增接口，再进行下面操作
                //判断idString是否undefined，如果是赋值成0
                if (curData.parentIdString === undefined) {
                  curData.parentIdString = "0";
                }
                const res = await updateMenu(curData);
                if (res.code == 0) {
                  chores();
                }
              }
            } catch (error) {
              console.error("Error update menu:", error);
              toast(`${title}菜单时出错`, { type: "error" });
            }
          }
        });
      }
    });
  }

  function handleDelete(row) {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要删除
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.title}
          </strong>
          吗？此菜单的子菜单也会被删除
        </p>
      ),
      beforeSure: async done => {
        const res = await deleteMenu(row.idString);
        if (res.code == 0) {
          toast(`已删除"${row.title}`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  }
  onMounted(() => {
    onSearch();
  });

  return {
    form,
    loading,
    columns,
    dataList,
    /** 搜索 */
    onSearch,
    /** 重置 */
    resetForm,
    /** 新增、修改菜单 */
    openDialog,
    /** 删除菜单 */
    handleDelete,
    handleSelectionChange
  };
}
