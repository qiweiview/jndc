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

//表格内容
const tableContent = (waterRef: Ref) => {
  //表格数据
  const tableData = ref([]);
  const loading = ref(false);

  //表格部分
  const columns: TableColumnList = [
    {
      label: "勾选列", // 如果需要表格多选，此处label必须设置
      type: "selection",
      fixed: "left",
      reserveSelection: true // 数据刷新后保留选项
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

  const resetPasswordConfirm = (row: any) => {
    ElMessageBox.confirm(
      `确认要<strong>重置</strong><strong style='color:var(--el-color-primary)'>${
        row.username
      }</strong>密码吗?`,
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
        const user: PureUser = {
          idS: row.idS
        };

        resetPassword(user).then(data => {
          //success类型的message
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
      .catch(() => {
        //todo 取消删除
      });
  };

  const handleDelete = (row: any) => {
    ElMessageBox.confirm(
      `确认要<strong>删除</strong><strong style='color:var(--el-color-primary)'>${
        row.username
      }</strong>用户吗?`,
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
        const user: PureUser = {
          idS: row.idS
        };
        deleteUser(user).then(() => {
          sendQueryPage();
        });
      })
      .catch(() => {
        //todo 取消删除
      });
  };

  /** 分页配置 */
  const pagination = reactive<PaginationProps>({
    pageSize: 10,
    currentPage: 1,
    pageSizes: [10, 20, 50, 100, 200, 500],
    total: 0,
    align: "right",
    background: true,
    size: "default"
  });

  /** 加载动画配置 */
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
    // svg: "",
    // background: rgba()
  });

  /** 撑满内容区自适应高度相关配置 */
  const adaptiveConfig: AdaptiveConfig = {
    /** 表格距离页面底部的偏移量，默认值为 `96` */
    offsetBottom: 96
    /** 是否固定表头，默认值为 `true`（如果不想固定表头，fixHeader设置为false并且表格要设置table-layout="auto"） */
    // fixHeader: true
    /** 页面 `resize` 时的防抖时间，默认值为 `60` ms */
    // timeout: 60
    /** 表头的 `z-index`，默认值为 `100` */
    // zIndex: 100
  };

  //每页显示数量改变
  function onSizeChange(val) {
    form.size = val;
    sendQueryPage();
  }

  //当前页改变
  function onCurrentChange(val) {
    form.current = val;
    sendQueryPage();
  }

  //生命周期
  onMounted(() => {
    delay().then(() => {
      sendQueryPage();

      // https://pure-admin-utils.netlify.app/hooks/useWatermark/useWatermark.html
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

  //查询表单
  const form = reactive({
    username: "",
    current: 1,
    size: 10
  });

  //查询列
  const searchColumn: PlusColumn[] = [
    {
      label: "用户名",
      prop: "name",
      valueType: "copy"
    }
  ];

  //发送查询请求
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

  //查询条件改变
  const handleChange = (values: any) => {
    form.username = values.name;
    sendQueryPage();
  };

  //查询按钮事件
  const handleSearch = (values: any) => {
    form.username = values.name;
    sendQueryPage();
  };

  //重置按钮事件
  const handleRest = () => {
    tableData.value = [];
  };

  //弹窗部分
  //定义弹框变量
  const visible = ref(false);

  //弹框表单
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
      fieldProps: {
        maxlength: 6
      }
    },
    {
      label: "密码",
      prop: "password",
      valueType: "copy",
      fieldProps: {
        showPassword: true,
        maxlength: 18
      }
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

  //返回所需内容组合的对象
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
