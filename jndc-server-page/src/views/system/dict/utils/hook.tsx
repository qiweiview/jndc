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
  addDict,
  deleteDict,
  listDict,
  updateDict
} from "@/api/system/dict/dict";

export function useDict() {
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const form = reactive({
    dictName: "",
    dictCode: "",
    status: "",
    size: pagination.pageSize,
    current: pagination.currentPage
  });
  const curRow = ref({ dictName: "" });
  const formRef = ref();
  const dataList = ref([]);
  const isShow = ref(false);
  const loading = ref(true);
  const isLinkage = ref(false);
  const switchLoadMap = ref({});
  // 数据项弹窗
  const dictDataDrawer = ref(false);

  const columns: TableColumnList = [
    {
      label: "字典名称",
      prop: "dictName",
      width: 200,
      fixed: "left"
    },
    {
      label: "字典编码",
      prop: "dictCode",
      width: 200,
      fixed: "left"
    },
    {
      label: "状态",
      cellRenderer: scope => (
        <el-switch
          size={scope.props.size === "small" ? "small" : "default"}
          loading={switchLoadMap.value[scope.index]?.loading}
          v-model={scope.row.status}
          active-value={0}
          inactive-value={1}
          active-text="已启用"
          inactive-text="已停用"
          style="--el-switch-on-color: #13ce66"
          inline-prompt
          onChange={() => onChange(scope as any)}
        />
      ),
      minWidth: 80
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
      width: 220,
      slot: "operation"
    }
  ];

  function onChange({ row, index }) {
    showDialog("提示", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要{row.status === 1 ? "停用" : "启用"}
          <strong style="color:var(--el-color-warning);margin:0 5px">
            {row.dictName}
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
        const res = await updateDict(row);
        if (res.code == 0) {
          switchLoadMap.value[index] = Object.assign(
            {},
            switchLoadMap.value[index],
            {
              loading: false
            }
          );
          toast(`已${row.status === 1 ? "停用" : "启用"}${row.dictName}`, {
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
            {row.dictName}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await deleteDict(row.id);
        if (res.code == 0) {
          toast(`已删除"${row.dictName}`, {
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
    const { code, data } = await listDict(toRaw(form));
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
      title: `${title}字典`,
      props: {
        formInline: {
          id: row?.id ?? null,
          dictName: row?.dictName ?? "",
          dictCode: row?.dictCode ?? "",
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
          toast(`您${title}了字典名称为${curData.dictName}的这条数据`, {
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
              const res = await addDict(curData);
              if (res.code == 0) {
                chores();
              }
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              const res = await updateDict(curData);
              if (res.code == 0) {
                chores();
              }
            }
          }
        });
      }
    });
  }
  function openDictData(row) {
    curRow.value = row;
    dictDataDrawer.value = true;
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
    // 数据项
    openDictData,
    handleDrawerUpdate
  };
}
