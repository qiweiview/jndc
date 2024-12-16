import dayjs from "dayjs";
import editForm from "../form/form.vue";
import { message as toast } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { showDialog } from "@/components/HalcyonDialog";
import type { FormItemProps } from "../utils/types";
import type { PaginationProps } from "@pureadmin/table";
import { deviceDetection } from "@pureadmin/utils";
import { h, onMounted, reactive, ref, toRaw } from "vue";
import {
  addRole,
  deleteRole,
  listRole,
  updateRole
} from "@/api/system/role/role";

export function useRole() {
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const form = reactive({
    roleName: "",
    roleCode: "",
    status: "",
    size: pagination.pageSize,
    current: pagination.currentPage
  });
  const curRow = ref({ roleName: "" });

  const formRef = ref();
  const dataList = ref([]);
  const isShow = ref(false);
  const loading = ref(true);
  const isLinkage = ref(false);
  const switchLoadMap = ref({});
  const roleMenuDrawer = ref(false);
  const columns: TableColumnList = [
    {
      label: "角色名称",
      prop: "roleName",
      width: 200,
      fixed: "left"
    },
    {
      label: "角色编码",
      prop: "roleCode",
      width: 240,
      fixed: "left"
    },
    {
      label: "状态",
      cellRenderer: scope => (
        <el-switch
          size={scope.props.size === "small" ? "small" : "default"}
          loading={switchLoadMap.value[scope.index]?.loading}
          v-model={scope.row.status}
          style="--el-switch-on-color: #13ce66"
          active-value={0}
          inactive-value={1}
          active-text="已启用"
          inactive-text="已停用"
          inline-prompt
          onChange={() => onChange(scope as any)}
        />
      ),
      minWidth: 80
    },
    {
      label: "绑定用户数",
      prop: "userCount",
      width: 100
    },
    {
      label: "创建时间",
      prop: "createTime",
      minWidth: 160,
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
    },
    {
      label: "备注",
      prop: "remark",
      minWidth: 160
    },

    {
      label: "操作",
      fixed: "right",
      width: 250,
      slot: "operation"
    }
  ];

  function onChange({ row, index }) {
    showDialog("提示", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要{row.status === 1 ? "停用" : "启用"}
          <strong style="color:var(--el-color-warning);margin:0 5px">
            {row.roleName}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        switchLoadMap.value[index] = Object.assign(
          {},
          switchLoadMap.value[index],
          {
            loading: true
          }
        );
        const res = await updateRole(row);
        switchLoadMap.value[index] = Object.assign(
          {},
          switchLoadMap.value[index],
          {
            loading: false
          }
        );
        if (res.code == 0) {
          toast(`已${row.status === 1 ? "停用" : "启用"}${row.roleName}`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      },
      closeCallBack: ({ args }) => {
        if (args?.command !== "sure") {
          row.status === 0 ? (row.status = 1) : (row.status = 0);
        }
      }
    });
  }

  function handleDelete(row) {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要删除
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.roleName}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await deleteRole(row.idString);
        if (res.code == 0) {
          toast(`已删除"${row.roleName}`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  }

  function handleSizeChange(val: number) {
    pagination.currentPage = 1;
    pagination.pageSize = val;
    onSearch();
  }

  function handleCurrentChange(val: number) {
    pagination.currentPage = val;
    onSearch();
  }

  function handleSelectionChange(val) {
    console.log("handleSelectionChange", val);
  }

  async function onSearch() {
    loading.value = true;
    form.current = pagination.currentPage;
    form.size = pagination.pageSize;
    const { code, data } = await listRole(toRaw(form));
    if (code != 0) {
    } else {
      dataList.value = data.records;
      pagination.total = data.total;
      pagination.pageSize = data.size;
      pagination.currentPage = data.current;
    }
    loading.value = false;
  }

  const resetForm = formEl => {
    if (!formEl) return;
    formEl.resetFields();
    onSearch();
  };

  function openDialog(title = "新增", row?: FormItemProps) {
    addDialog({
      title: `${title}角色`,
      props: {
        formInline: {
          id: row?.id ?? null,
          idString: row?.idString ?? null,
          roleName: row?.roleName ?? "",
          roleCode: row?.roleCode ?? "",
          status: row?.status ?? 0,
          remark: row?.remark ?? ""
        }
      },
      width: "40%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef }),
      beforeSure: (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          toast(`您${title}了角色名称为${curData.roleName}的这条数据`, {
            type: "success"
          });
          done(); // 关闭弹框
          onSearch(); // 刷新表格数据
        }
        FormRef.validate(async valid => {
          if (valid) {
            console.log("curData", curData);
            // 表单规则校验通过
            if (title === "新增") {
              // 实际开发先调用新增接口，再进行下面操作
              const res = await addRole(curData);
              if (res.code == 0) {
                chores();
              }
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              const res = await updateRole(curData);
              if (res.code == 0) {
                chores();
              }
            }
          }
        });
      }
    });
  }
  // 菜单权限
  function openRoleMenu(row) {
    curRow.value = row;
    roleMenuDrawer.value = true;
  }

  function handleDrawerUpdate(newVal: boolean) {
    roleMenuDrawer.value = newVal;
  }
  onMounted(async () => {
    onSearch();
  });

  return {
    form,
    isShow,
    curRow,
    loading,
    columns,
    dataList,
    isLinkage,
    pagination,
    roleMenuDrawer,
    onSearch,
    resetForm,
    openDialog,
    handleDelete,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange,
    openRoleMenu,
    handleDrawerUpdate
  };
}
