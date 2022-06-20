<template>
    <div>
        <h3>数据源</h3>
        <el-form label-width="80px">
            <el-form-item label="名称">
                <el-input v-model="database.name"></el-input>
            </el-form-item>
            <el-form-item label="类型">
                <el-select v-model="database.sourceType" placeholder="类型">
                    <el-option v-for="at in db_type" :key="at.value" :label="at.name" :value="at.value"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item v-show="database.sourceType=='CSV'">
                <el-table
                        :data="database.content" style="width: 100%">
                    <el-table-column label="数据">
                        <template slot-scope="scope">
                            <i class="el-icon-time"></i>
                            <span style="margin-left: 10px">{{ scope.row }}</span>
                        </template>
                    </el-table-column>
                </el-table>
                <input @change="changeFile" ref="input_file" type="file" id="file" name="file" accept=".csv"/>
            </el-form-item>
            <el-form-item>
                <el-button @click="submit" type="primary" size="mini">保存</el-button>
                <el-button size="mini">重置</el-button>
            </el-form-item>
        </el-form>

    </div>
</template>

<script>
    export default {
        name: "db_vertex",
        data() {
            return {
                id: "",
                database: {
                    name: '',
                    sourceType: '',
                    content: [],
                    minColumn: 0
                },
                db_type: [{
                    name: 'CSV',
                    value: 'CSV',
                }
                ]
            }
        },
        methods: {
            async changeFile(f) {
                const data = await f.target.files[0].text()
                let ar = data.split(/\r?\n/)
                this.$set(this.database, "content", ar);
                let min = 0;
                ar.forEach(x => {
                    let sa = x.split(',')
                    if (sa.length < min || min == 0) {
                        min = sa.length
                    }
                })
                this.$set(this.database, "minColumn", min);
                this.$refs['input_file'].value = null
            },
            getStoreData() {
                return this.database
            },
            submit() {
                this.$emit('submit', this.id, this.getStoreData())
            },
            loadStoreData(data) {
                this.database = data
            },
            load(id, data) {
                this.id = id
                this.loadStoreData(data)
            },
            initInnerData() {
                let obj = {name: 'd1', sourceType: 'CSV', type: 'db'}
                return obj
            },
            acceptCheck() {
                return false
            }
        },
        mounted() {

        }
    }
</script>

<style scoped>

</style>
