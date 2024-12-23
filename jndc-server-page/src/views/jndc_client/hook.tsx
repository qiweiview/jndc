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
  updateOperation,
  connectOperation,
  forceStopOperation
} from "@/api/jndc_client/api";
import { formatDate } from "@/utils/date_format";
import { convertNumber } from "@/utils/value_format/number_fornmat";
import jndcLog from "@/views/jndc_log/index.vue";
import jndcClientService from "@/views/jndc_client_service/index.vue";
import {
  getLabelTypeByValue,
  getLabelByValue
} from "@/views/jndc_client/form/enums";

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
    current: pagination.currentPage
  });

  const curRow = ref({ dictName: "" });
  const formRef = ref();
  const dataList = ref([]);
  const isShow = ref(false);
  const loading = ref(true);
  const isLinkage = ref(false);

  // 数据项弹窗
  const dictDataDrawer = ref(false);

  //表格
  const columns: TableColumnList = [
    {
      label: "客户端名称",
      prop: "clientName",
      minWidth: 120,
      fixed: "left"
    },
    {
      label: "服务主机",
      prop: "serverHost",
      minWidth: 120
    },
    {
      label: "服务端口",
      prop: "serverPort",
      minWidth: 120
    },
    {
      label: "客户端状态",
      prop: "clientStatus",
      minWidth: 120,
      cellRenderer: ({ row }) => (
        <el-tag type={getLabelTypeByValue(row.clientStatus)} effect="plain">
          {getLabelByValue(row.clientStatus)}
        </el-tag>
      )
    },
    {
      label: "自动重连",
      prop: "autoReconnect",
      minWidth: 120,
      formatter(row, column, cellValue) {
        return convertNumber(cellValue);
      }
    },
    {
      label: "重连间隔",
      prop: "reconnectInterval",
      minWidth: 120
    },
    {
      label: "重连次数限制",
      prop: "reconnectMaxTimes",
      minWidth: 120
    },
    {
      label: "唯一id",
      prop: "uniqueId",
      minWidth: 360
    },
    {
      label: "创建时间",
      prop: "createTime",
      minWidth: 160,
      formatter: (row, column, cellValue) => {
        return formatDate(cellValue);
      }
    },
    {
      label: "修改时间",
      prop: "updateTime",
      minWidth: 160,
      formatter: (row, column, cellValue) => {
        return formatDate(cellValue);
      }
    },
    {
      label: "操作",
      fixed: "right",
      minWidth: 220,
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

  const connectCheck = (row: FormItemProps) => {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要启动
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.clientName}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await connectOperation(row.idString);
        if (res.code == 0) {
          toast(`已启动"${row.clientName}`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  };
  const forceStopCheck = (row: FormItemProps) => {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要停止
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.clientName}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await forceStopOperation(row.idString);
        if (res.code == 0) {
          toast(`已停止"${row.clientName}`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  };

  async function onSearch() {
    loading.value = true;
    form.current = pagination.currentPage;
    form.size = pagination.pageSize;
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

  function openLogDialog(row) {
    addDialog({
      title: "运行日志",
      fullscreen: true,
      hideFooter: true,
      contentRenderer: () => jndcLog,
      props: {
        sourceIdString: row.idString
      }
    });
  }
  function openServiceDialog(row) {
    addDialog({
      title: "注册服务",
      fullscreen: true,
      hideFooter: true,
      contentRenderer: () => jndcClientService,
      props: {
        sourceIdString: row.idString
      }
    });
  }

  function openDialog(title = "新增", row?: FormItemProps) {
    addDialog({
      title: `${title}`,
      props: {
        formInline: {
          autoReconnect: row?.autoReconnect ?? 1,
          clientName: row?.clientName ?? "test",
          clientRemark: row?.clientRemark ?? null,
          clientStatus: row?.clientStatus ?? "pause",
          createTime: row?.createTime ?? null,
          disguisedProtocol: row?.disguisedProtocol ?? null,
          id: row?.id ?? null,
          reconnectInterval: row?.reconnectInterval ?? 15,
          reconnectMaxTimes: row?.reconnectMaxTimes ?? -1,
          serverHost: row?.serverHost ?? "127.0.0.1",
          serverPort: row?.serverPort ?? 9866,
          uniqueId: row?.uniqueId ?? null,
          updateTime: row?.updateTime ?? null,
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
    isShow,
    curRow,
    loading,
    columns,
    dataList,
    isLinkage,
    pagination,
    dictDataDrawer,
    connectCheck,
    forceStopCheck,
    openServiceDialog,
    openLogDialog,
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
