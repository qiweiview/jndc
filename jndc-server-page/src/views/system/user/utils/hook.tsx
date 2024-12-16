import dayjs from "dayjs";
import editForm from "../form/form.vue";
import { message as toast } from "@/utils/message";
import { addDialog } from "@/components/ReDialog";
import { showDialog } from "@/components/HalcyonDialog";
import type { FormItemProps } from "./types";
import type { PaginationProps } from "@pureadmin/table";
import { deviceDetection, isAllEmpty, isEqualArray } from "@pureadmin/utils";
import { zxcvbn } from "@zxcvbn-ts/core";
import { computed, h, onMounted, reactive, ref, toRaw, watch } from "vue";
import { usePublicHooks } from "@/views/system/hooks";
import roleForm from "../form/role.vue";
import { useUserStoreHook } from "@/store/modules/user";
import {
  addUser,
  deleteUser,
  getUserDetail,
  getUserList,
  resetPassword,
  updateUser,
  updateUserStutus
} from "@/api/system/user/list";
import { ElForm, ElFormItem, ElInput, ElProgress } from "element-plus";
import ReCropperPreview from "@/components/ReCropperPreview/index";
import { useUpload } from "@/utils/upload/upload";
import { REGEXP_PWD } from "./rule";
import {
  assignRoleForUser,
  getRoleIdsByUserId
} from "@/api/auth/permission/permission";
import { listAllSimpleRole } from "@/api/system/role/role";

export function useUserList() {
  const { uploadFileByBack } = useUpload();
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const form = reactive({
    username: "",
    email: "",
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
  const switchLoadMap = ref({});
  const { switchStyle } = usePublicHooks();
  const buttonClass = computed(() => {
    return [
      "!h-[20px]",
      "reset-margin",
      "!text-gray-500",
      "dark:!text-white",
      "dark:hover:!text-primary"
    ];
  });
  const pwdRuleFormRef = ref();
  // 重置的新密码
  const pwdForm = reactive({
    password: ""
  });
  const pwdProgress = [
    { color: "#e74242", text: "非常弱" },
    { color: "#EFBD47", text: "弱" },
    { color: "#ffa500", text: "一般" },
    { color: "#1bbf1b", text: "强" },
    { color: "#008000", text: "非常强" }
  ];
  // 当前密码强度（0-4）
  const curScore = ref();
  // 头像
  const cropRef = ref();
  // 文件信息
  const fileInfo = ref();

  const allRoles = ref([]);

  const columns: TableColumnList = [
    {
      label: "用户名",
      prop: "username",
      width: 150,
      fixed: "left"
    },
    {
      label: "用户来源",
      prop: "registerSource"
    },
    {
      label: "头像",
      prop: "avatar",
      width: 100,
      cellRenderer: ({ row }) => (
        <el-image
          lazy
          style="width: 50px; height: 50px;border-radius: 50%"
          src={row.avatar}
          preview-src-list={[row.avatar]}
          preview-teleported
          fit="cover"
        />
      )
    },
    {
      label: "状态",
      cellRenderer: scope => (
        <el-switch
          disabled={
            useUserStoreHook().isSelf(scope.row.id) && scope.row.status === 0
          }
          size={scope.props.size === "small" ? "small" : "default"}
          loading={switchLoadMap.value[scope.index]?.loading}
          v-model={scope.row.status}
          active-value={0}
          inactive-value={1}
          active-text="正常"
          inactive-text="冻结"
          inline-prompt
          style={switchStyle.value}
          onChange={() => onChange(scope as any)}
        />
      ),
      minWidth: 80
    },

    {
      label: "性别",
      prop: "gender",
      width: 80,
      cellRenderer: ({ row, props }) => (
        <el-tag
          size={props.size}
          type={
            row.gender === 0 ? "info" : row.gender === 1 ? "primary" : "danger"
          }
          effect="plain"
        >
          {row.gender === 0 ? "未知" : row.gender === 1 ? "男" : "女"}
        </el-tag>
      )
    },
    {
      label: "创建时间",
      prop: "createTime",
      width: 180,
      showOverflowTooltip: true,
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
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
          确认要{row.status === 1 ? "冻结" : "启用"}
          <strong style="color:var(--el-color-warning);margin:0 5px">
            {row.username}
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
        console.log("row", row);
        const res = await updateUserStutus({
          id: row.id,
          idString: row.idString,
          status: row.status
        });
        switchLoadMap.value[index] = Object.assign(
          {},
          switchLoadMap.value[index],
          {
            loading: false
          }
        );
        if (res.code == 0) {
          toast(`已${row.status === 1 ? "冻结" : "启用"}${row.username}`, {
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
            {row.username}
          </strong>
          吗?
        </p>
      ),
      beforeSure: async done => {
        const res = await deleteUser([row.idString]);
        if (res.code == 0) {
          toast(`已删除"${row.userName}`, {
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
    const { code, data } = await getUserList(toRaw(form));
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
      title: `${title}用户`,
      props: {
        formInline: {
          id: row?.id ?? null,
          idString: row?.idString ?? null,
          username: row?.username ?? "",
          password: row?.password ?? "",
          nickname: row?.nickname ?? "",
          status: row?.status ?? 0,
          gender: row?.gender ?? 0,
          email: row?.email ?? "",
          phone: row?.phone ?? "13088888888",
          intro: row?.intro ?? "",
          birthday: row?.birthday ?? null
        }
      },
      width: "40%",
      top: "5vh",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef }),
      open: async ({ options }) => {
        if (title !== "新增") {
          const res = await getUserDetail(row.idString);
          if (res.code == 0) {
            rowDetail.value = res.data;
          }
          // 手动更新
          options.props.formInline.email = rowDetail.value.email;
          options.props.formInline.phone = rowDetail.value.phone;
        }
      },
      beforeSure: (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          toast(`您${title}了用户名为${curData.username}的这条数据`, {
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
              const res = await addUser(curData);
              if (res.code == 0) {
                chores();
              }
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              const res = await updateUser(curData);
              if (res.code == 0) {
                chores();
              }
            }
          }
        });
      }
    });
  }
  /** 上传头像 */
  function handleUpload(row) {
    addDialog({
      title: "裁剪、上传头像",
      width: "40%",
      closeOnClickModal: false,
      fullscreen: deviceDetection(),
      contentRenderer: () =>
        h(ReCropperPreview, {
          ref: cropRef,
          imgSrc: row.avatar,
          onCropper: info => {
            fileInfo.value = info;
            console.log("info", info);
          }
        }),
      beforeSure: done => {
        const file = new File([fileInfo.value.blob], fileInfo.value.info.name, {
          type: fileInfo.value.blob.type
        });
        uploadFileByBack(file)
          .then(async (res: any) => {
            if (res.code == 0) {
              row.avatar = res.data;
              const updateRes = await updateUser(row);
              if (updateRes.code == 0) {
                toast(`修改成功`, {
                  type: "success"
                });
              } else {
                toast(updateRes.message, { type: "error" });
              }
            } else {
              toast(res.message, { type: "error" });
            }
          })
          .catch(err => {
            toast(err.message, { type: "error", duration: 3000 });
          })
          .finally(() => {
            done(); // 关闭弹框
            onSearch(); // 刷新表格数据
          });
      },
      closeCallBack: () => cropRef.value.hidePopover()
    });
  }
  /** 重置密码 */
  function handlePasswordReset(row) {
    addDialog({
      title: `重置 ${row.username} 用户的密码`,
      width: "30%",
      draggable: true,
      closeOnClickModal: false,
      fullscreen: deviceDetection(),
      contentRenderer: () => (
        <>
          <ElForm ref={pwdRuleFormRef} model={pwdForm}>
            <ElFormItem
              prop="password"
              rules={{
                required: true,
                validator: (rule, value, callback) => {
                  if (value === "") {
                    callback(new Error("请输入密码"));
                  } else if (!REGEXP_PWD.test(value)) {
                    callback(
                      new Error("密码格式必须在6至20个字符之间且不能包含中文")
                    );
                  } else {
                    callback();
                  }
                },
                trigger: "blur"
              }}
            >
              <ElInput
                clearable
                show-password
                type="password"
                v-model={pwdForm.password}
                placeholder="请输入新密码"
              />
            </ElFormItem>
          </ElForm>
          <div class="mt-4 flex mb-4">
            {pwdProgress.map(({ color, text }, idx) => (
              <div
                class="w-[19vw]"
                style={{ marginLeft: idx !== 0 ? "4px" : 0 }}
              >
                <ElProgress
                  striped
                  striped-flow
                  duration={curScore.value === idx ? 6 : 0}
                  percentage={curScore.value >= idx ? 100 : 0}
                  color={color}
                  stroke-width={10}
                  show-text={false}
                />
                <p
                  class="text-center"
                  style={{ color: curScore.value === idx ? color : "" }}
                >
                  {text}
                </p>
              </div>
            ))}
          </div>
        </>
      ),
      closeCallBack: () => (pwdForm.password = ""),
      beforeSure: done => {
        pwdRuleFormRef.value.validate(async valid => {
          if (valid) {
            // 表单规则校验通过
            toast(`已成功重置 ${row.username} 用户的密码`, {
              type: "success"
            });
            const res = await resetPassword({
              id: row.id,
              idString: row.idString,
              password: pwdForm.password
            });
            if (res.code == 0) {
              pwdForm.password = "";
              done(); // 关闭弹框
              onSearch(); // 刷新表格数据
            }
          }
        });
      }
    });
  }

  /** 分配角色 */
  async function handleRole(row) {
    // 用户所拥有的角色
    const roleIds = ref([]);
    addDialog({
      title: `分配 ${row.username} 用户的角色`,

      width: "450px",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(roleForm),
      open: async ({ options }) => {
        // 选中的角色列表
        const res = await getRoleIdsByUserId(row.idString);
        if (res.code == 0) {
          roleIds.value = res.data;
        }
        // 手动更新
        options.props.formInline.roleIds = roleIds.value;
      },
      props: {
        formInline: {
          username: row?.username ?? "",
          allRoles: allRoles.value,
          roleIds: roleIds.value
        }
      },
      beforeSure: async (done, { options }) => {
        const curData = options.props.formInline;
        if (curData.roleIds.length == 0) {
          toast("请选择至少一个角色", { type: "error" });
          return;
        }
        if (isEqualArray(curData.roleIds, roleIds.value)) {
          toast("所选角色未改变", { type: "error" });
          return;
        }
        const { code } = await assignRoleForUser({
          userId: row.id,
          roleIds: curData.roleIds
        });
        if (code == 0) {
          toast("角色分配成功", { type: "success" });
          done(); // 关闭弹框
          onSearch(); // 刷新表格数据
        }
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
  watch(
    pwdForm,
    ({ password }) =>
      (curScore.value = isAllEmpty(password) ? -1 : zxcvbn(password).score)
  );

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
    handleSelectionChange,
    handleUpload,
    handlePasswordReset,
    handleRole
  };
}
