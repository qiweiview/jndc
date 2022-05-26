<template>
    <div>
        <!--        <el-form ref="form" :model="action" label-width="80px">-->
        <!--            <el-form-item label="操作名称">-->
        <!--                <el-input v-model="action.name"></el-input>-->
        <!--            </el-form-item>-->
        <!--            <el-form-item label="操作类型">-->
        <!--                <el-select v-model="action.type" placeholder="请选择活动区域">-->
        <!--                    <el-option v-for="at in actionTypes"     :key="at.a_value" :label="at.a_name" :value="at.a_value"></el-option>-->
        <!--                </el-select>-->
        <!--            </el-form-item>-->
        <!--            <el-form-item>-->
        <!--                <el-button type="primary" size="mini" >保存</el-button>-->
        <!--                <el-button  size="mini" >重置</el-button>-->
        <!--            </el-form-item>-->
        <!--        </el-form>-->
        操作
    </div>
</template>

<script>
    export default {
        name: "action",
        data() {
            return {
                id: "",
                action: {
                    name: "",
                    type: ""
                },
                actionTypes: [
                    {
                        a_name: "求和",
                        a_value: "sum",
                    }
                ],
                sourceColumns: []
            }
        },
        props: {
            current: Object,
        },
        mounted() {
        },
        methods: {
            loadStoreData(data) {
                console.log(data)
            },
            load(id, data) {
                this.id = id
                this.loadStoreData(data)
            },
            initInnerData() {
                let obj = {}
                return obj
            }
        }
    }
</script>

<style scoped>

</style>
