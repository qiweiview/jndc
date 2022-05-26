<template>
    <div>
        <h3>聚合</h3>
        <el-table
                height="600px"
                :data="supportData.aggColumnsSelect"
                style="width: 100%">
            <el-table-column
                    label="列序号"
                    width="180">
                <template slot-scope="scope">
                    <el-select v-model="scope.row.index" placeholder="请选择">
                        <el-option
                                v-for="item in supportData.aggColumns"
                                :key="item.index"
                                :label="item.index"
                                :value="item.index">
                        </el-option>
                    </el-select>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            type="danger"
                            @click="remove(scope.row)">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button style="margin-top: 15px" @click="addOne" size="mini">添加列</el-button>
        <el-button @click="submit" type="primary" size="mini">保存</el-button>
    </div>
</template>

<script>
    export default {
        name: "agg_vertex.vue",
        data() {
            return {
                id: "",
                supportData: {}

            }
        },
        methods: {
            remove(row) {
                let na = []
                this.aggColumnsSelect.forEach(x => {
                    if (x != row) {
                        na.push(x)
                    }
                })
                this.$set(this, 'aggColumnsSelect', na)
            },
            addOne() {
                this.supportData.aggColumnsSelect.push({index: 0})
            },
            submit() {
                this.$emit('submit', this.id, this.getStoreData())
            },
            /*起始点数据绑定至目标点*/
            bindSourceData(sourceId, source, targetId, target) {
                if (source.type == 'db') {
                    target.minColumn = source.minColumn
                    let newAggColumns = []
                    for (let i = 0; i < target.minColumn; i++) {
                        let fi = {index: i}
                        newAggColumns.push(fi)
                    }
                    target.aggColumns = newAggColumns
                    target.nearSource = {type: 'db', id: sourceId, key: 'db' + sourceId}
                } else if (source.type == 'merge_vertex') {
                    target.minColumn = source.unionSources.length
                    let newAggColumns = []
                    for (let i = 0; i < target.minColumn; i++) {
                        let fi = {index: i}
                        newAggColumns.push(fi)
                    }
                    target.aggColumns = newAggColumns
                    target.nearSource = {type: 'merge', id: sourceId, key: 'merge' + sourceId}
                } else {
                    console.log('上游未选择数据')
                }

            },
            getStoreData() {
                let newObject = JSON.parse(JSON.stringify(this.supportData))
                return newObject
            },
            loadStoreData(data) {
                this.$set(this, 'supportData', data)
            },
            load(id, data) {
                this.id = id
                this.loadStoreData(data)
            },
            initInnerData() {
                let obj = {
                    type: 'agg',
                    aggColumns: [],
                    aggColumnsSelect: [],
                    minColumn: 0,
                    nearSource: {
                        type: '',
                        id: ''
                    }//最近上游数据
                }
                return obj
            },
            acceptCheck(componentType) {
                let allow = ['db_vertex', 'merge_vertex']
                let check = false
                for (let i = 0; i < allow.length; i++) {
                    if (allow[i] == componentType) {
                        check = true
                    }
                }
                return check
            }
        },
        mounted() {

        }
    }
</script>

<style scoped>

</style>
