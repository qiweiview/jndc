import dayjs from "dayjs";
import editForm from "../form/form.vue";
import { message as toast } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { showDialog } from "@/components/HalcyonDialog";
import type { FormItemProps } from "./types";
import type { PaginationProps } from "@pureadmin/table";
import { deviceDetection } from "@pureadmin/utils";

import { computed, h, onMounted, reactive, ref, toRaw } from "vue";
import {
  addNotice,
  deleteNotice,
  getNoticeDetail,
  getNoticeList,
  updateNotice
} from "@/api/system/notice/notice";
import { listAllSimpleRole } from "@/api/system/role/role";
import { openDetail } from "@/components/NoticeDetail/index";

export function useSystemNotice() {
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const form = reactive({
    title: "",
    type: "",
    status: "",
    size: pagination.pageSize,
    current: pagination.currentPage
  });
  const curRow = ref();
  const formRef = ref();
  const dataList = ref([]);
  const isShow = ref(false);
  const loading = ref(true);
  const isLinkage = ref(false);
  const buttonClass = computed(() => {
    return [
      "!h-[20px]",
      "reset-margin",
      "!text-gray-500",
      "dark:!text-white",
      "dark:hover:!text-primary"
    ];
  });

  const allRoles = ref([]);

  const columns: TableColumnList = [
    {
      label: "标题",
      prop: "title",
      width: 150,
      fixed: "left",
      showOverflowTooltip: true,
      cellRenderer: ({ row, props }) => (
        <el-Text
          type="primary"
          size={props.size}
          style="cursor: pointer"
          onClick={() => {
            openDetail(row, {});
          }}
        >
          {row.title}
        </el-Text>
      )
    },
    {
      label: "类型",
      prop: "type",
      width: 100,
      fixed: "left",
      cellRenderer: ({ row, props }) => (
        <el-tag
          size={props.size}
          type={row.type === 1 ? "primary" : "danger"}
          effect="plain"
        >
          {row.type == 1 ? "通知" : "公告"}
        </el-tag>
      )
    },
    {
      label: "内容",
      prop: "content",
      width: 170,
      showOverflowTooltip: true
    },
    {
      label: "状态",
      prop: "status",
      width: 80,
      cellRenderer: ({ row, props }) => (
        <el-tag
          size={props.size}
          type={row.status === 0 ? "success" : "danger"}
          effect="plain"
        >
          {row.status == 0 ? "正常" : "关闭"}
        </el-tag>
      )
    },
    {
      label: "通知角色",
      prop: "roleNameArr",
      width: 160,
      showOverflowTooltip: true
    },
    {
      label: "创建人",
      prop: "creator",
      width: 100
    },
    {
      label: "创建时间",
      prop: "createTime",
      showOverflowTooltip: true,
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
    },
    {
      label: "修改时间",
      prop: "updateTime",
      showOverflowTooltip: true,
      cellRenderer: ({ row }) => (
        <span>
          {row.updateTime
            ? dayjs(row.updateTime).format("YYYY-MM-DD HH:mm:ss")
            : ""}
        </span>
      )
    },
    {
      label: "修改人",
      prop: "modifier",
      width: 100
    },
    {
      label: "操作",
      fixed: "right",
      width: 160,
      slot: "operation"
    }
  ];
  function handleDelete(row) {
    showDialog("警告", {
      contentRenderer: () => (
        <p style="text-align: center;margin-bottom:20px">
          确认要删除
          <strong style="color:var(--el-color-danger);margin:0 5px">
            {row.title}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await deleteNotice([row.id]);
        if (res.code == 0) {
          toast(`已删除"${row.title}`, {
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
    const { code, data } = await getNoticeList(toRaw(form));
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

  function openDialog(title = "新增", row?: FormItemProps) {
    const rowDetail = ref({} as FormItemProps);
    addDialog({
      title: `${title}通知公告`,
      props: {
        formInline: {
          id: row?.id ?? null,
          title: row?.title ?? "",
          content: row?.content ?? "",
          status: row?.status ?? 0,
          type: row?.type ?? 1,
          allRoles: allRoles.value,
          editorHeight: "300px"
        }
      },
      width: "65%",
      top: "3vh",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      fullscreenCallBack: ({ options }) =>
        options.fullscreen
          ? (options.props.formInline.editorHeight = "400px")
          : (options.props.formInline.editorHeight = "300px"),
      contentRenderer: () => h(editForm, { ref: formRef }),
      open: async ({ options }) => {
        if (title !== "新增") {
          const res = await getNoticeDetail(row.id);
          if (res.code == 0) {
            rowDetail.value = res.data;
          }
          // 手动更新
          options.props.formInline.content = rowDetail.value.content;
          options.props.formInline.status = rowDetail.value.status;
          options.props.formInline.type = rowDetail.value.type;
          options.props.formInline.roleIds = rowDetail.value.roleIds;
        }
      },
      beforeSure: (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          toast(`您${title}了通知公告为${curData.title}的这条数据`, {
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
              const res = await addNotice(curData);
              if (res.code == 0) {
                chores();
              }
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              const res = await updateNotice(curData);
              if (res.code == 0) {
                chores();
              }
            }
          }
        });
      }
    });
  }
  async function getRoleList() {
    const res = await listAllSimpleRole();
    if (res.code == 0) {
      allRoles.value = res.data;
    }
  }
  onMounted(() => {
    onSearch();
    getRoleList();
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
    buttonClass,
    onSearch,
    resetForm,
    openDialog,
    handleDelete,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange
  };
}
