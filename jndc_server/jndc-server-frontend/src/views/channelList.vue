<template>
    <el-row style="padding: 10px">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">

            <el-tabs v-model="activeName" @tab-click="clickTab">
                <el-tab-pane label="隧道列表" name="a">
                    <el-input clearable v-model="searchKey" placeholder="筛选隧道编号或IP"
                              style="width:20%" @change="getServerChannelTable"></el-input>
                    <el-button size="mini" @click="getServerChannelTable" style="margin-left:15px">查询</el-button>
                    <el-table :data="displayArray">
                        <el-table-column label="隧道编号">
                            <template slot-scope="scope"><span>{{ scope.row.id }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="隧道来源">
                            <template slot-scope="scope"><span>{{ scope.row.channelClientIp }}:{{ scope.row.channelClientPort }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column width="100px" label="服务数">
                            <template slot-scope="scope"><span
                                    style="cursor: pointer;color: deepskyblue;text-align: left"
                                    @click="toServicePage(scope.row.id)"
                            >{{ scope.row.supportServiceNum }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column width="150px" label="最后心跳时间">
                            <template slot-scope="scope"><span style="">{{ new Date(scope.row.lastHearBeatTimeStamp).Format("yyyy-MM-dd HH:mm:ss")  }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button size="mini" type="danger"
                                           @click="closeChannelByServer(scope.row.id,scope.row.supportServiceNum)">断 开
                                </el-button>
                                <el-button size="mini" type="success"
                                           @click="sendHeartBeat(scope.row.id)">发 送 心 跳
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
                <el-tab-pane label="中断记录" name="b">
                    <el-button @click="getChannelRecord" size="mini" type="info">刷 新</el-button>
                    <el-button @click="clearChannelRecord" size="mini" type="danger">清 空</el-button>
                    <el-table :data="channelRecordArray" max-height="85vh">
                        <el-table-column label="隧道编号">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.channelId }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="隧道客户端IP">
                            <template slot-scope="scope"><span style="">{{ scope.row.ip }}</span></template>
                        </el-table-column>
                        <el-table-column label="隧道客户端端口">
                            <template slot-scope="scope"><span style="">{{ scope.row.port }}</span></template>
                        </el-table-column>
                        <el-table-column label="连接时间">
                            <template slot-scope="scope"><span
                                    style="text-align: center">{{  new Date(scope.row.timeStamp).Format("yyyy-MM-dd HH:mm:ss")}}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            @size-change="sizeChange"
                            @current-change="pageChange"
                            background
                            :page-sizes="[10, 15, 30, 100]"
                            :page-size="recordRows"
                            layout="sizes, prev, pager, next"
                            :total="chanelRecordTotal">
                    </el-pagination>
                </el-tab-pane>
            </el-tabs>

        </el-col>
    </el-row>
</template>

<script>
    import request from "@/config/requestConfig";
    import websocket from "@/config/webSocketTool";

    Date.prototype.Format = function (fmt) {
        let o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "H+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (let k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    export default {
        name: "channelList",
        data() {
            return {
                searchKey: '',
                storeArray: [],
                displayArray: [],
                channelRecordArray: [],
                activeName: 'a',
                recordCurrentPage: 1,
                chanelRecordTotal: 0,
                recordRows: 10
            }
        },
        methods: {
            toServicePage(clientId) {
                this.$router.push({path: '/management/services', query: {clientId: clientId}})
            },
            clearChannelRecord() {
                let _this = this
                this.$confirm('清空连接纪录后将不可恢复?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {

                    request({
                        url: '/clearChannelRecord',
                        method: 'post',
                        data: {}
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            _this.$message.success(response.message);
                            _this.getChannelRecord()
                        } else {
                            _this.$message.error(response.message);
                        }
                    }).catch(() => {
                    })

                }).catch(() => {

                });
            },
            clickTab(tab) {
                if (tab.name == "b") {
                    this.getChannelRecord()
                }


            },
            sizeChange(size) {
                this.recordRows = size
                this.getChannelRecord()
            },
            pageChange(page) {
                this.recordCurrentPage = page
                this.getChannelRecord()
            },
            getChannelRecord() {
                request({
                    url: '/getChannelRecord',
                    method: 'post',
                    data: {page: this.recordCurrentPage, rows: this.recordRows}
                }).then(response => {
                    this.channelRecordArray = response.data
                    this.chanelRecordTotal = response.total
                }).catch(() => {
                })
            },
            sendHeartBeat(id) {
                request({
                    url: '/sendHeartBeat',
                    method: 'post',
                    data: {id: id}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    if (response.code == 200) {
                        //refresh force
                        this.$message.success(response.message);
                    } else {
                        this.$message.error(response.message);
                    }
                }).catch(() => {
                })
            },
            closeChannelByServer(channelId, ports) {
                let _this = this

                this.$confirm('断开隧道后,隧道提供的：' + ports + '项服务将不再被使用，是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {


                    request({
                        url: '/closeChannelByServer',
                        method: 'post',
                        data: {id: channelId}
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            _this.$message.success(response.message);
                            _this.getServerChannelTable()
                        } else {
                            _this.$message.error(response.message);
                        }
                    }).catch(() => {
                    })


                }).catch(() => {

                });


            },
            getServerChannelTable() {
                // eslint-disable-next-line no-constant-condition
                if (true) {
                    const loading = this.$loading({
                        lock: true,
                        text: 'Loading',
                        spinner: 'el-icon-loading',
                        background: 'rgba(0, 0, 0, 0.7)'
                    })


                    request({
                        url: '/getServerChannelTable',
                        method: 'post',
                        data: {}
                    }).then(response => {
                        this.storeArray = response
                        this.conditionalRendering()
                        loading.close()
                    }).catch(() => {
                        loading.close()
                    })
                } else {
                    this.conditionalRendering()
                }


            },
            conditionalRendering() {
                if ('' == this.searchKey) {
                    this.displayArray = this.storeArray
                } else {
                    let na = []
                    this.storeArray.forEach(x => {
                        if (x.id.indexOf(this.searchKey) !== -1 || x.channelClientIp.indexOf(this.searchKey) !== -1) {
                            na.push(x)
                        }
                    });
                    this.displayArray = na
                }
            }

        }
        , mounted() {
            this.getServerChannelTable()
            websocket.registerPage('channelList', '隧道列表', this.getServerChannelTable)
        }
    }
</script>

<style scoped>

</style>
