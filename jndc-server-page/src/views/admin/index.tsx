import type { Ref } from "vue";
import { onMounted, reactive, ref } from "vue";
import { message } from "@/utils/message";
import type {
  AdaptiveConfig,
  LoadingConfig,
  PaginationProps
} from "@pureadmin/table";
import { delay, useWatermark } from "@pureadmin/utils";
import type { PlusColumn } from "plus-pro-components";

//表格内容
const tableContent = (waterRef: Ref) => {
  //表格数据
  const tableData = ref([]);
  const loading = ref(false);
  const tableSize = ref("default");

  const columns: TableColumnList = [
    {
      type: "selection",
      align: "left"
    },
    {
      label: "ID",
      prop: "id",
      align: "center"
    },
    {
      label: "日期",
      prop: "date",
      align: "center"
    },
    {
      label: "姓名",
      prop: "name",
      align: "center"
    },
    {
      label: "地址",
      prop: "address",
      align: "center"
    },
    {
      label: "操作",
      align: "center",
      cellRenderer: ({ index, row }) => (
        <>
          <el-button
            size="small"
            type="danger"
            onClick={() => handleDelete(index + 1, row)}
          >
            Delete
          </el-button>
        </>
      )
    }
  ];

  const handleDelete = (index: number, row) => {
    message(`您删除了第 ${index} 行，数据为：${JSON.stringify(row)}`);
  };

  /** 分页配置 */
  const pagination = reactive<PaginationProps>({
    pageSize: 10,
    currentPage: 1,
    pageSizes: [10, 20, 50, 100, 200, 500],
    total: 300,
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

  function onSizeChange(val) {
    loadingConfig.text = `页面尺寸变化为${val}...`;
    loading.value = true;
    delay(600).then(() => {
      loading.value = false;
    });
  }

  function onCurrentChange(val) {
    loadingConfig.text = `正在加载第${val}页...`;
    loading.value = true;
    delay(600).then(() => {
      loading.value = false;
    });
  }

  onMounted(() => {
    delay().then(() => {
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

  //返回所需内容组合的对象
  return {
    tableData,
    loading,
    columns,
    tableSize,
    pagination,
    loadingConfig,
    adaptiveConfig,
    onSizeChange,
    onCurrentChange
  };
};

//搜索内容
const searchContent = () => {
  const form = reactive({
    id: "",
    date: "",
    name: "",
    address: ""
  });

  const searchColumn: PlusColumn[] = [
    {
      label: "名称",
      prop: "name",
      valueType: "copy",
      tooltip: "名称最多显示6个字符"
    }
  ];

  const handleChange = (values: any) => {
    console.log(values, "change");
  };
  const handleSearch = (values: any) => {
    console.log(values, "search");
  };
  const handleRest = () => {
    console.log("handleRest");
  };

  return {
    form,
    searchColumn,
    handleChange,
    handleSearch,
    handleRest
  };
};

export { tableContent, searchContent };
