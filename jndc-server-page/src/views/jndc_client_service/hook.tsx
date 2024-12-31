import editForm from "./form/form.vue";
import { message as toast } from "@/utils/message";
import { addDialog } from "@/components/ReDialog/index";
import { showDialog } from "@/components/HalcyonDialog";
import type { FormItemProps } from "./form/types";
import type { PaginationProps } from "@pureadmin/table";
import { deviceDetection } from "@pureadmin/utils";
import { h, onMounted, reactive, ref, toRaw } from "vue";
import {
  unRegisterOperation,
  listOperation,
  addOperation,
  deleteOperation,
  updateOperation
} from "@/api/jndc_client_service/api";
import { formatDate } from "@/utils/date_format";
import { getLabelTypeByValue, getLabelByValue } from "./form/enums";

export function useHook(clientId: string) {
  //注册clientId
  if (clientId) {
    console.log("clientId", clientId);
  }

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
    clientIdString: null
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
      label: "服务名称",
      prop: "serviceName"
    },
    {
      label: "服务主机",
      prop: "serviceHost"
    },
    {
      label: "服务端口",
      prop: "servicePort"
    },
    {
      label: "期望绑定端口",
      prop: "expectPort"
    },
    {
      label: "服务状态",
      prop: "serviceStatus",
      cellRenderer: ({ row }) => (
        <el-tag type={getLabelTypeByValue(row.serviceStatus)} effect="plain">
          {getLabelByValue(row.serviceStatus)}
        </el-tag>
      )
    },
    {
      label: "是否自动注册",
      prop: "autoRegister",
      formatter: (row, column, cellValue) => {
        return cellValue === 1 ? "是" : "否";
      }
    },
    {
      label: "唯一服务id",
      prop: "serviceUniqueId"
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

  const unRegisterCheck = (row: FormItemProps) => {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要取消注册
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.serviceName}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await unRegisterOperation(row.idString);
        if (res.code == 0) {
          toast(`已取消注册"${row.serviceName}`, {
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

  function openDialog(title = "新增", row?: FormItemProps) {
    //处理组建值问题
    let autoRegister: number;
    if (typeof row !== "undefined") {
      if (row.autoRegister === 1) {
        autoRegister = 0;
      } else {
        autoRegister = 1;
      }
    } else {
      autoRegister = 1;
    }

    addDialog({
      title: `${title}`,
      props: {
        formInline: {
          autoRegister: autoRegister,
          clientId: row?.clientId ?? null,
          createTime: row?.createTime ?? null,
          expectPort: row?.expectPort ?? 1443,
          id: row?.id ?? null,
          serviceHost: row?.serviceHost ?? "baidu.com",
          serviceMode: row?.serviceMode ?? null,
          serviceName: row?.serviceName ?? "baidu",
          servicePort: row?.servicePort ?? 443,
          serviceProtocol: row?.serviceProtocol ?? null,
          serviceStatus: row?.serviceStatus ?? "unregister",
          serviceUniqueId: row?.serviceUniqueId ?? null,
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

            curData.clientIdString = clientId;
            //处理autoRegister
            curData.autoRegister = curData.autoRegister === 0 ? 1 : 0;

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
    unRegisterCheck,
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
