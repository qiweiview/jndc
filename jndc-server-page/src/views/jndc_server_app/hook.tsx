import editForm from "./form/form.vue";
import { message as toast } from "@/utils/message";
import { addDialog } from "@/components/ReDialog/index";
import { showDialog } from "@/components/HalcyonDialog";
import type { FormItemProps, MockMetaData } from "./form/types";
import type { PaginationProps } from "@pureadmin/table";
import { deviceDetection } from "@pureadmin/utils";
import { h, onMounted, reactive, ref, toRaw } from "vue";
import {
  listOperation,
  addOperation,
  deleteOperation,
  updateOperation
} from "@/api/jndc_server_app/api";
import { getLabelByValue, getLabelTypeByValue } from "./form/enums";
import { listenOperation, pauseOperation } from "@/api/jndc_server_app/api";

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
    serverIdString: null,
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
      label: "id",
      prop: "idString",
      fixed: "left",
      width: 200
    },
    {
      label: "监听域名",
      prop: "bindHost"
    },
    {
      label: "监听端口",
      prop: "bindPort"
    },
    {
      label: "监听类型",
      prop: "bindType",
      width: 200
    },
    {
      label: "监听状态",
      prop: "bindStatus",
      cellRenderer: ({ row }) => (
        <el-tag type={getLabelTypeByValue(row.bindStatus)} effect="plain">
          {getLabelByValue(row.bindStatus)}
        </el-tag>
      )
    },
    {
      label: "创建时间",
      prop: "createTime",
      width: 220
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
    addDialog({
      title: `${title}`,
      props: {
        formInline: {
          bindHost: row?.bindHost ?? "0.0.0.0",
          bindPort: row?.bindPort ?? 1234,
          bindStatus: row?.bindStatus ?? "pause",
          bindType: row?.bindType ?? "mock-server",
          createTime: row?.createTime ?? null,
          id: row?.id ?? null,
          serverId: row?.serverId ?? null,
          sourceClientId: row?.sourceClientId ?? null,
          sourceServiceId: row?.sourceServiceId ?? null,
          idString: row?.idString ?? null,
          metaData: row?.metaData ?? null
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
            // 表单规则校验通过
            if (title === "新增") {
              if (!curData.metaData) {
              }

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

  const pauseCheck = (row: FormItemProps) => {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要停止
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.bindType}
          </strong>
          监听吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await pauseOperation(row.idString);
        if (res.code == 0) {
          toast(`已停止"${row.bindType}监听`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  };

  const listenCheck = (row: FormItemProps) => {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要开启
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.bindType}
          </strong>
          监听吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await listenOperation(row.idString);
        if (res.code == 0) {
          toast(`开始"${row.bindType}监听`, {
            type: "success"
          });
        }
        done(); // 关闭弹框
        onSearch();
      }
    });
  };

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
    listenCheck,
    pauseCheck,
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
