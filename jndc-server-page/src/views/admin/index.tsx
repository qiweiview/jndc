import type { Ref } from "vue";
import { onMounted, reactive, ref } from "vue";
import { message } from "@/utils/message";
import type {
  AdaptiveConfig,
  LoadingConfig,
  PaginationProps
} from "@pureadmin/table";
import { delay, useWatermark } from "@pureadmin/utils";
import type { PlusColumn, FieldValues } from "plus-pro-components";
import {
  queryUserPage,
  createUser,
  deleteUser,
  resetPassword,
  type PureUser
} from "@/api/admin/pure_user";
import { ElMessageBox } from "element-plus";
import { addNotice, type ListItem } from "@/layout/components/lay-notice/data";

// Table Data & Columns
const tableContent = (waterRef: Ref) => {
  // Table State
  const tableData = ref([]);
  const loading = ref(false);

  // Table Columns Definition
  const columns: TableColumnList = [
    {
      label: "勾选列",
      type: "selection",
      fixed: "left",
      reserveSelection: true
    },
    {
      label: "ID",
      prop: "idS",
      align: "center",
      width: 180
    },
    {
      label: "姓名",
      prop: "username",
      align: "center",
      width: 80
    },
    {
      label: "创建日期",
      prop: "createDate",
      align: "center"
    },
    {
      label: "修改日期",
      prop: "updateDate",
      align: "center"
    },
    {
      label: "操作",
      align: "center",
      fixed: "right",
      slot: "operation"
    }
  ];

  // Table Pagination Configuration
  const pagination = reactive<PaginationProps>({
    pageSize: 10,
    currentPage: 1,
    pageSizes: [10, 20, 50, 100, 200, 500],
    total: 0,
    align: "right",
    background: true,
    size: "default"
  });

  // Loading Spinner Configuration
  const loadingConfig = reactive<LoadingConfig>({
    text: "正在加载第一页...",
    viewBox: "-10, -10, 50, 50",
    spinner: `
      <path class="path" d="
        M 30 15
        L 28 17
        M 25.61 25.61
        A 15 15, 0, 0, 1, 15 30
        A 15 15, 0, 1, 1, 27.99 7.5
        L 15 15
      " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
    `
  });

  // Adaptive Table Height Configuration
  const adaptiveConfig: AdaptiveConfig = {
    offsetBottom: 96
  };

  // Form State for Pagination & Search
  const form = reactive({
    username: "",
    current: 1,
    size: 10
  });

  // Search Columns Configuration
  const searchColumn: PlusColumn[] = [
    {
      label: "用户名",
      prop: "name",
      valueType: "copy"
    }
  ];

  // Form & Query Handlers
  const sendQueryPage = () => {
    loading.value = true;
    queryUserPage(form)
      .then(data => {
        tableData.value = data.records;
        pagination.total = data.total;
      })
      .finally(() => {
        loading.value = false;
      });
  };

  const handleChange = (values: any) => {
    form.username = values.name;
    sendQueryPage();
  };

  const handleSearch = (values: any) => {
    form.username = values.name;
    sendQueryPage();
  };

  const handleRest = () => {
    tableData.value = [];
  };

  // Table Pagination & Size Change Handlers
  function onSizeChange(val: number) {
    form.size = val;
    sendQueryPage();
  }

  function onCurrentChange(val: number) {
    form.current = val;
    sendQueryPage();
  }

  // Lifecycle Hook to Fetch Data & Set Watermark
  onMounted(() => {
    delay().then(() => {
      sendQueryPage();
      const { setWatermark } = useWatermark(
        waterRef.value.getTableDoms().tableWrapper
      );
      setWatermark("注意数据安全", {
        font: "16px Microsoft YaHei",
        globalAlpha: 0.8,
        forever: true,
        width: 240,
        height: 90
      });
    });
  });

  // Dialog State & Form
  const visible = ref(false);
  const dialogForm = ref<FieldValues>({});

  const dialog_rule = {
    username: [
      { required: true, message: "请输入用户名", trigger: "blur" },
      { min: 1, message: "用户名长度不能小于1位", trigger: "blur" },
      { max: 6, message: "用户名长度不能大于6位", trigger: "blur" },
      {
        pattern: /^[a-zA-Z0-9_]{1,6}$/,
        message: "用户名只能包含字母、数字和下划线",
        trigger: "blur"
      }
    ],
    password: [
      { required: true, message: "请输入密码", trigger: "blur" },
      { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
      { max: 18, message: "密码长度不能大于18位", trigger: "blur" },
      {
        pattern: /^[a-zA-Z0-9_]{6,18}$/,
        message: "密码只能包含字母、数字和下划线",
        trigger: "blur"
      }
    ]
  };

  const dialog_form_columns: PlusColumn[] = [
    {
      label: "用户名",
      prop: "username",
      valueType: "copy",
      fieldProps: { maxlength: 6 }
    },
    {
      label: "密码",
      prop: "password",
      valueType: "copy",
      fieldProps: { showPassword: true, maxlength: 18 }
    }
  ];

  const closeDialog = () => {
    dialogForm.value = {};
    visible.value = false;
  };

  const dialogConfirm = form => {
    createUser(form).then(data => {
      message("创建成功:" + data);
      sendQueryPage();
      closeDialog();
    });
  };

  // Password Reset & User Deletion Handlers
  const resetPasswordConfirm = (row: any) => {
    ElMessageBox.confirm(
      `确认要<strong>重置</strong><strong style='color:var(--el-color-primary)'>${row.username}</strong>密码吗?`,
      "系统提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        draggable: true
      }
    )
      .then(() => {
        const user: PureUser = { idS: row.idS };
        resetPassword(user).then(data => {
          message("重置成功:" + data, { type: "success" });
          const listItem: ListItem = {
            avatar: "",
            title: "重置密码",
            datetime: new Date().toLocaleString(),
            description: `用户${row.username}的密码已重置,重置为：${data}`,
            extra: "已完成",
            status: "info",
            type: "success"
          };
          addNotice(listItem);
        });
      })
      .catch(() => {});
  };

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(
      `确认要<strong>删除</strong><strong style='color:var(--el-color-primary)'>${row.username}</strong>用户吗?`,
      "系统提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        draggable: true
      }
    )
      .then(() => {
        const user: PureUser = { idS: row.idS };
        deleteUser(user).then(() => sendQueryPage());
      })
      .catch(() => {});
  };

  // Return All Required Methods & States
  return {
    visible,
    dialogForm,
    dialogConfirm,
    dialog_rule,
    dialog_form_columns,
    tableData,
    loading,
    columns,
    pagination,
    loadingConfig,
    adaptiveConfig,
    onSizeChange,
    onCurrentChange,
    form,
    searchColumn,
    handleChange,
    handleSearch,
    handleDelete,
    handleRest,
    resetPasswordConfirm
  };
};

export { tableContent };
