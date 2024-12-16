<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from "vue";
import { message as toast } from "@/utils/message";
import { delay, getKeyList, useResizeObserver } from "@pureadmin/utils";
import ElTreeLine from "@/components/ReTreeLine";
import { handleTree } from "@/utils/tree";
import { listSimpleMenu } from "@/api/system/menu/menu";
import { getMenuType } from "@/views/system/menu/utils/types";
import {
  assignForRole,
  listMenuIdByRoleId
} from "@/api/auth/permission/permission";

defineOptions({
  name: "SystemRoleMenu"
});
const props = defineProps({
  // 控制值
  roleMenuDrawer: {
    type: Boolean,
    default: false
  },
  // 角色对象
  roleObj: {
    type: Object,
    default: null
  }
});
const loading = ref(true);
const menusTree = ref([]);
const treeRef = ref();
const treeSearchValue = ref("");
const treeContainerRef = ref();
const treeHeight = ref(500);
const isLinkage = ref(false);
const isExpandAll = ref(false);
const isSelectAll = ref(false);
// 展开用的数组
let allMenuIds = [];

const treeProps = {
  value: "id",
  title: "title",
  children: "children",
  type: "type"
};
const emit = defineEmits(["updateRoleMenuDrawer"]);
const localRoleMenuDrawer = ref(props.roleMenuDrawer);
watch(
  () => props.roleMenuDrawer,
  newVal => {
    localRoleMenuDrawer.value = newVal;
  }
);
// 监听本地值的变化
watch(localRoleMenuDrawer, newVal => {
  emit("updateRoleMenuDrawer", newVal);
});
watch(isExpandAll, val => {
  val
    ? treeRef.value.setExpandedKeys(allMenuIds)
    : treeRef.value.setExpandedKeys([]);
});

watch(isSelectAll, val => {
  console.log("isSelectAll", allMenuIds);
  val
    ? treeRef.value.setCheckedKeys(allMenuIds)
    : treeRef.value.setCheckedKeys([]);
});

onMounted(() => {
  useResizeObserver(treeContainerRef, async () => {
    await nextTick();
    delay(60).then(() => {
      treeHeight.value = treeContainerRef.value.clientHeight;
    });
  });
});
async function getSimpleMenuList() {
  const { code, data, message } = await listSimpleMenu();
  if (code != 0) {
    // message(message, { type: "error" });
  } else {
    // 全部菜单ID
    allMenuIds = getKeyList(data, "idString");
    menusTree.value = handleTree(data);
  }
}

async function handleDrawerOpen(roleObj) {
  loading.value = true;
  const { code, data, message } = await listMenuIdByRoleId(roleObj.idString);
  if (code != 0) {
    toast(message, { type: "error" });
  } else {
    console.log("setCheckedKeys", data);
    isSelectAll.value = false;
    isLinkage.value = false;
    treeRef.value.setCheckedKeys(data);
    loading.value = false;
    isExpandAll.value = true;
  }
}
function onQueryChanged(query: string) {
  treeRef.value!.filter(query);
  if (query == "") {
    isExpandAll.value = true;
  }
}
function filterMethod(query: string, node: any) {
  return node.title.includes(query);
}
function handleDrawerClose() {}
async function save() {
  const checkedKeys = treeRef.value.getCheckedKeys();
  const checkedNodes = treeRef.value.getCheckedNodes();
  // 判断一个节点的父节点是否被选中
  const isParentChecked = (node: any) => {
    console.log("judge", node);

    if (!node.parentId || node.parentId == "0") {
      return true; // 没有父节点，假定根节点，假设父节点已选中
    }

    return checkedKeys.includes(node.parentId); // 检查父节点是否在选中节点中
  };

  // 检查是否有选中节点存在未选中的父节点
  const hasUncheckedParents = checkedNodes.some(node => !isParentChecked(node));
  if (hasUncheckedParents) {
    toast("选择了子节点但没有选择父节点，请修正后再保存", { type: "warning" });
    return;
  }
  const { code, message } = await assignForRole({
    roleId: props.roleObj.id,
    menuIds: checkedKeys
  });
  if (code != 0) {
    toast(message, { type: "error" });
  } else {
    toast("分配成功", { type: "success" });
    localRoleMenuDrawer.value = false;
  }
}

getSimpleMenuList();
</script>

<template>
  <div>
    <el-drawer
      v-model="localRoleMenuDrawer"
      :title="props.roleObj.roleName"
      direction="rtl"
      size="40%"
      class="role-menu-drawer"
      :close-on-click-modal="false"
      @open="handleDrawerOpen(props.roleObj)"
      @closed="handleDrawerClose"
    >
      <div style="height: 100%">
        <div class="header">
          <el-input
            v-model="treeSearchValue"
            placeholder="请输入菜单名称（为角色分配菜单后自动分配首页）"
            style="width: 70%"
            clearable
            @input="onQueryChanged"
          />
        </div>
        <div ref="treeContainerRef" v-loading="loading" style="height: 90%">
          <el-tree-v2
            ref="treeRef"
            :height="treeHeight"
            :data="menusTree"
            :props="treeProps"
            show-checkbox
            :indent="30"
            :check-strictly="!isLinkage"
            :filter-method="filterMethod"
            ><template v-slot:default="{ node }">
              <el-tree-line :node="node" :showLabelLine="true">
                <template v-slot:node-label>
                  <span class="text-sm">
                    {{ node.data.title }}
                  </span>
                </template>
                <template v-slot:after-node-label>
                  <el-text :type="getMenuType(node.data.type)" effect="plain">
                    {{ getMenuType(node.data.type, true) }}
                  </el-text>
                </template>
              </el-tree-line>
            </template>
          </el-tree-v2>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-between">
          <div>
            <el-button @click="localRoleMenuDrawer = false">取消</el-button>
            <el-button type="primary" @click="save">保存</el-button>
          </div>
          <div>
            <el-checkbox v-model="isExpandAll" border label="展开/折叠" />
            <el-checkbox v-model="isSelectAll" border label="全选/全不选" />
            <el-checkbox v-model="isLinkage" border label="父子联动" />
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<style lang="scss">
.role-menu-drawer {
  .el-drawer__body {
    padding-top: 0;
    padding-bottom: 0;
  }
  .header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 20px;
  }
}
</style>
