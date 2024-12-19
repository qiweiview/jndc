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
  updateOperation
} from "@/api/jndc_server_accept_history/api";

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
      label: "客户端id",
      prop: "clientId"
    },
    {
      label: "连接时间",
      prop: "connectTime"
    },
    {
      label: "创建时间",
      prop: "createTime"
    },
    {
      label: "",
      prop: "id"
    },
    {
      label: "中断时间",
      prop: "interruptTime"
    },
    {
      label: "来源ip",
      prop: "sourceIp"
    },
    {
      label: "来源端口",
      prop: "sourcePort"
    },
    {
      label: "修改时间",
      prop: "updateTime"
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
      clientId: row?.clientId ?? null,
      connectTime: row?.connectTime ?? null,
      createTime: row?.createTime ?? null,
      id: row?.id ?? null,
      interruptTime: row?.interruptTime ?? null,
      sourceIp: row?.sourceIp ?? null,
      sourcePort: row?.sourcePort ?? null,
      updateTime: row?.updateTime ?? null,
    idString:row?.idString?? null,
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
