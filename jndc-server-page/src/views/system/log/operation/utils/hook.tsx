import dayjs from "dayjs";
import { message as toast } from "@/utils/message";
import { showDialog } from "@/components/HalcyonDialog";
import type { PaginationProps } from "@pureadmin/table";
import { onMounted, reactive, ref, toRaw } from "vue";
import {
  clearOperLog,
  deleteOperLog,
  listOperLog
} from "@/api/system/log/oper";
import { DictCode, getDictDataName } from "@/utils/dict";
import { addDialog } from "@/components/ReDialog";
import Detail from "../detail.vue";

export function useOperLog() {
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const queryForm = reactive({
    title: "",
    status: null,
    operTimeArr: null,
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
      width: 100,
      showOverflowTooltip: true
    },
    {
      label: "操作模块",
      prop: "title",
      minWidth: 100
    },
    {
      label: "请求接口",
      prop: "operUrl"
    },
    {
      label: "操作人员",
      prop: "operUsername",
      showOverflowTooltip: true
    },
    {
      label: "操作地址",
      prop: "operLocation",
      showOverflowTooltip: true
    },
    {
      label: "操作IP",
      prop: "operIp",
      showOverflowTooltip: true
    },

    {
      label: "业务类型",
      prop: "businessType",
      showOverflowTooltip: true,
      cellRenderer: ({ row }) => (
        <el-tag type="primary">
          {getDictDataName(
            DictCode.OPER_BUSINESS_TYPE,
            row.businessType.toString()
          )}
        </el-tag>
      )
    },

    {
      label: "操作状态",
      prop: "status",
      showOverflowTooltip: true,
      cellRenderer: ({ row }) => (
        <el-tag type={row.status == 0 ? "success" : "danger"}>
          {row.status == 0 ? "成功" : "失败"}
        </el-tag>
      )
    },
    {
      label: "耗时",
      prop: "costTime",
      showOverflowTooltip: true,
      formatter: ({ costTime }) => `${costTime}ms`
    },
    {
      label: "操作时间",
      prop: "operTime",
      showOverflowTooltip: true,
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
    },
    {
      label: "操作",
      fixed: "right",
      width: 100,
      slot: "operation"
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
        const res = await clearOperLog();
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
        const res = await deleteOperLog(selectedIds);
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
    tableRef.value.getTableRef().clearSelection();
  }
  function openDetail(row) {
    addDialog({
      title: "系统操作日志详情",
      fullscreen: true,
      hideFooter: true,
      contentRenderer: () => Detail,
      props: {
        data: row
      }
    });
  }
  async function onSearch() {
    loading.value = true;
    queryForm.current = pagination.currentPage;
    queryForm.size = pagination.pageSize;
    if (queryForm.operTimeArr !== null && queryForm.operTimeArr.length == 2) {
      queryForm.operTimeArr[0] = dayjs(queryForm.operTimeArr[0]).format(
        "YYYY-MM-DD HH:mm:ss"
      );
      queryForm.operTimeArr[1] = dayjs(queryForm.operTimeArr[1]).format(
        "YYYY-MM-DD HH:mm:ss"
      );
    }
    const { code, data } = await listOperLog(toRaw(queryForm));
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
    handleSelectionCancel,
    openDetail
  };
}
