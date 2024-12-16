import dayjs from "dayjs";
import { message as toast } from "@/utils/message";
import { showDialog } from "@/components/HalcyonDialog";
import type { PaginationProps } from "@pureadmin/table";
import { onMounted, reactive, ref, toRaw } from "vue";
import {
  clearLoginLog,
  deleteLoginLog,
  listLoginLog
} from "@/api/system/log/login";

export function useLoginLog() {
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const queryForm = reactive({
    account: "",
    status: null,
    loginTimeArr: null,
    size: pagination.pageSize,
    current: pagination.currentPage
  });
  const isShow = ref(false);
  const curRow = ref();
  const dataList = ref([]);
  const loading = ref(true);
  const tableRef = ref();
  const selectedNum = ref(0);
  const columns: TableColumnList = [
    {
      label: "勾选列", // 如果需要表格多选，此处label必须设置
      type: "selection",
      fixed: "left",
      reserveSelection: true // 数据刷新后保留选项
    },
    {
      label: "日志编号",
      prop: "id",
      fixed: "left",
      showOverflowTooltip: true
    },
    {
      label: "用户账号",
      prop: "account"
    },
    {
      label: "IP地址",
      prop: "ipAddress",
      showOverflowTooltip: true
    },
    {
      label: "登录地点",
      prop: "loginLocation",
      showOverflowTooltip: true
    },
    {
      label: "操作系统",
      prop: "os",
      showOverflowTooltip: true
    },

    {
      label: "浏览器",
      prop: "browser",
      showOverflowTooltip: true
    },

    {
      label: "登录状态",
      prop: "status",
      showOverflowTooltip: true,
      cellRenderer: ({ row }) => (
        <el-tag type={row.status == 0 ? "success" : "danger"}>
          {row.status == 0 ? "成功" : "失败"}
        </el-tag>
      )
    },
    {
      label: "登录信息",
      prop: "msg",
      showOverflowTooltip: true
    },
    {
      label: "登录时间",
      prop: "loginTime",
      showOverflowTooltip: true,
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
    }
  ];
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
    selectedNum.value = val.length;
    // 重置表格高度
    tableRef.value.setAdaptive();
  }

  function handleClear() {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要清空登录日志吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await clearLoginLog();
        if (res.code == 0) {
          toast("清空成功", {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  }
  function handlebatchDelete() {
    // 返回当前选中的行
    const curSelected = tableRef.value.getTableRef().getSelectionRows();
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要删除编号为
          {curSelected.map((item, index) => (
            <strong style="color:var(--el-color-danger);margin:0 5px">
              {item.id}
              {index < curSelected.length - 1 ? "、" : ""}
            </strong>
          ))}
          的日志吗?
        </p>
      ),
      beforeSure: async done => {
        const selectedIds = curSelected.map(item => item.id);
        const res = await deleteLoginLog(selectedIds);
        if (res.code == 0) {
          toast(`删除成功`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        tableRef.value.getTableRef().clearSelection();
        onSearch();
      }
    });
  }
  function handleSelectionCancel() {
    selectedNum.value = 0;
    // 用于多选表格，清空用户的选择
    tableRef.value.getTableRef().clearSelection();
  }

  async function onSearch() {
    loading.value = true;
    queryForm.current = pagination.currentPage;
    queryForm.size = pagination.pageSize;
    if (queryForm.loginTimeArr !== null && queryForm.loginTimeArr.length == 2) {
      queryForm.loginTimeArr[0] = dayjs(queryForm.loginTimeArr[0]).format(
        "YYYY-MM-DD HH:mm:ss"
      );
      queryForm.loginTimeArr[1] = dayjs(queryForm.loginTimeArr[1]).format(
        "YYYY-MM-DD HH:mm:ss"
      );
    }
    const { code, data } = await listLoginLog(toRaw(queryForm));
    if (code == 0) {
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

  onMounted(() => {
    onSearch();
  });

  return {
    isShow,
    queryForm,
    curRow,
    loading,
    columns,
    dataList,
    pagination,
    selectedNum,
    tableRef,
    onSearch,
    resetForm,
    handlebatchDelete,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange,
    handleClear,
    handleSelectionCancel
  };
}
