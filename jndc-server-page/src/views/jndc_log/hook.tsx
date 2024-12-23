import editForm from "./form/form.vue";
import { message as toast } from "@/utils/message";
import { addDialog } from "@/components/ReDialog/index";
import { showDialog } from "@/components/HalcyonDialog";
import type { FormItemProps } from "./form/types";
import type { PaginationProps } from "@pureadmin/table";
import { deviceDetection } from "@pureadmin/utils";
import { h, onMounted, reactive, ref, toRaw } from "vue";
import {
  listOperation,
  addOperation,
  deleteOperation,
  deleteBatchOperation,
  updateOperation
} from "@/api/jndc_log/api";

import { formatDate } from "@/utils/date_format";

export function useHook() {
  //分页
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });

  //表单
  const form = reactive({
    size: pagination.pageSize,
    current: pagination.currentPage,
    sourceIdString: null
  });

  const tableRef = ref();
  const curRow = ref({ dictName: "" });
  const formRef = ref();
  const dataList = ref([]);
  const isShow = ref(false);
  const loading = ref(true);
  const isLinkage = ref(false);
  const selectedNum = ref(0);

  // 数据项弹窗
  const dictDataDrawer = ref(false);

  //表格
  const columns: TableColumnList = [
    {
      label: "勾选列", // 如果需要表格多选，此处label必须设置
      type: "selection",
      fixed: "left",
      reserveSelection: true // 数据刷新后保留选项
    },
    {
      label: "日志日期",
      prop: "logTime",
      formatter: (row, column, cellValue) => {
        return formatDate(cellValue);
      },
      width: 160
    },
    {
      label: "日志类型",
      prop: "logType",
      width: 120
    },
    {
      label: "日志内容",
      prop: "logContent",
      align: "left",
      headerAlign: "center"
    },
    {
      label: "操作",
      fixed: "right",
      width: 220,
      slot: "operation"
    }
  ];

  function handleDelete(row) {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要删除
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.idString}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await deleteOperation(row.idString);
        if (res.code == 0) {
          toast(`已删除"${row.idString}`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  }

  function handleSelectionCancel() {
    selectedNum.value = 0;
    tableRef.value.getTableRef().clearSelection();
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
        const selectedIds = curSelected.map(item => item.idString);
        const res = await deleteBatchOperation(selectedIds);
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

  async function onSearch() {
    loading.value = true;
    form.current = pagination.currentPage;
    form.size = pagination.pageSize;
    if (form.sourceIdString == "") {
      delete form.sourceIdString;
    }
    const { code, data } = await listOperation(toRaw(form));
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
      title: `${title}`,
      props: {
        formInline: {
          id: row?.id ?? null,
          logContent: row?.logContent ?? null,
          logTime: row?.logTime ?? null,
          logType: row?.logType ?? null,
          sourceId: row?.sourceId ?? null,
          idString: row?.idString ?? null
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
          toast(`您${title}了这条数据`, {
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
              const res = await addOperation(curData);
              if (res.code == 0) {
                chores();
              }
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              const res = await updateOperation(curData);
              if (res.code == 0) {
                chores();
              }
            }
          }
        });
      }
    });
  }

  function handleDrawerUpdate(newVal: boolean) {
    dictDataDrawer.value = newVal;
  }

  onMounted(async () => {
    onSearch();
  });

  return {
    form,
    tableRef,
    handlebatchDelete,
    handleSelectionCancel,
    selectedNum,
    isShow,
    curRow,
    loading,
    columns,
    dataList,
    isLinkage,
    pagination,
    dictDataDrawer,
    onSearch,
    resetForm,
    openDialog,
    handleDelete,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange,
    handleDrawerUpdate
  };
}
