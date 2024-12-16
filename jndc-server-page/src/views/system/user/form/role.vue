<script setup lang="ts">
import { onMounted, ref } from "vue";
import ReCol from "@/components/ReCol";
import { RoleFormProps } from "../utils/types";

const props = withDefaults(defineProps<RoleFormProps>(), {
  formInline: () => ({
    userId: null,
    username: "",
    roleIds: [],
    allRoles: []
  })
});

const newFormInline = ref(props.formInline);
let copyRoleOptions = [];

function searchRole(query) {
  newFormInline.value.allRoles = copyRoleOptions.filter(item =>
    item.roleName.includes(query)
  );
}
onMounted(() => {
  copyRoleOptions = props.formInline.allRoles;
});
</script>

<template>
  <el-form :model="newFormInline">
    <el-row :gutter="30">
      <re-col>
        <el-form-item label="用户名称" prop="username">
          <el-input v-model="newFormInline.username" disabled />
        </el-form-item>
      </re-col>
      <re-col>
        <el-form-item label="角色列表" prop="roleIds">
          <el-select
            v-model="newFormInline.roleIds"
            placeholder="请选择"
            class="w-full"
            clearable
            multiple
            collapse-tags
            :max-collapse-tags="3"
            filterable
            :filter-method="searchRole"
          >
            <el-option
              v-for="(item, index) in newFormInline.allRoles"
              :key="index"
              :value="item.id"
              :label="item.roleName"
            >
              {{ item.roleName }}
            </el-option>
          </el-select>
        </el-form-item>
      </re-col>
    </el-row>
  </el-form>
</template>
