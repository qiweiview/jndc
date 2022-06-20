<template>
    <el-row style="padding: 10px">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <el-tabs v-model="activeName" @tab-click="clickTab">
                <el-tab-pane label="端口监听" name="a">
                    <el-input clearable v-model="searchKey" placeholder="筛选服务器端口号"
                              style="width:20%" @change="getServerPortList"></el-input>
                    <el-button size="mini" @click="openAddPortDialog" type="success" style="margin-left:15px">添 加
                    </el-button>
                    <el-button size="mini" @click="getServerPortList" style="margin-left:15px">查 询</el-button>
                    <el-table :data="displayArray" style="margin: 0" max-height="700px">
                        <el-table-column label="监听端口" width="100px">
                            <template slot-scope="scope">
                                <span @click="routeToPortListPage(scope.row.port)"
                                      style="cursor: pointer;color: deepskyblue;text-align: center">
                                {{ scope.row.port }}
                                </span>
                            </template>
                        </el-table-column>
                        <el-table-column label="端口备注" width="200px">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.name==''?'无':scope.row.name }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="监听状态" width="100px">
                            <template slot-scope="scope">
                                <span v-if="scope.row.portEnable==1" style="color:#67C23A">监听中</span>
                                <span v-if="scope.row.portEnable==0" style="color:#F56C6C">未监听</span>
                                <span v-if="scope.row.portEnable==2" style="color:#67C23A">启动中</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="绑定隧道编号" width="300px">
                            <template slot-scope="scope"><span
                                    style="text-align: left">{{ scope.row.bindClientId }}</span>
                            </template>
                        </el-table-column>

                        <el-table-column label="处理请求时间段" width="200px">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.enableDateRange.split(",")[0]+' 至 '+scope.row.enableDateRange.split(",")[1] }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="端口状态" width="150px">
                            <template slot-scope="scope">
                                <span :style="{'text-align': 'left','color':checkPortStateByDateRange(scope.row.enableDateRange)?'green':'red'}">
                            {{checkPortStateByDateRange(scope.row.enableDateRange)?'处理请求':'不处理请求'}}
                            </span>
                            </template>
                        </el-table-column>
                        <el-table-column label="服务关联记录" width="200px">
                            <template slot-scope="scope"><span
                                    style="">{{ scope.row.routeTo==null?'未关联过服务':scope.row.routeTo}}</span></template>
                        </el-table-column>
                        <el-table-column fixed="right" label="操作" width="280px">
                            <template slot-scope="scope">
                                <el-tooltip class="item" effect="dark" content="修改处理请求时间段" placement="bottom">
                                    <el-button size="mini" type="info"
                                               @click="openDateRangeEditDialog(scope.row.id,scope.row.enableDateRange,scope.row.name)">
                                        编 辑
                                    </el-button>
                                </el-tooltip>

                                <el-button v-if="scope.row.portEnable==1" size="mini" type="warning"
                                           @click="stopServiceBind(scope.row.id)">停止
                                </el-button>

                                <el-tooltip class="item" effect="dark" content="启动端口监听" placement="bottom">
                                    <el-button v-if="scope.row.portEnable==0" size="mini" type="success"
                                               @click="openPortBindDialog(scope.row.id)">启动
                                    </el-button>
                                </el-tooltip>
                                <el-button v-if="scope.row.portEnable==2" size="mini" type="primary" disabled>{{
                                    '监听启动中...' }}
                                </el-button>
                                <el-tooltip class="item" effect="dark" content="移除端口监听" placement="bottom">
                                    <el-button v-if="scope.row.portEnable==0||scope.row.portEnable==2" size="mini"
                                               type="danger"
                                               @click="deleteServiceBindRecord(scope.row.id)">移除
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip class="item" effect="dark" content="重置(清空)服务关联" placement="bottom">
                                    <el-button v-if="scope.row.portEnable==0&&scope.row.routeTo!=null" size="mini"
                                               type="warning" @click="resetBindRecord(scope.row.id)">重置
                                    </el-button>
                                </el-tooltip>


                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>

            <el-dialog title="修改处理请求时间段" :visible.sync="dateRangeEditDialog" width="30%" :close-on-click-modal="false">
                <el-form label-position="top" :model="portMonitoring">
                    <el-form-item label="接收请求时间段">
                        <el-time-picker
                                value-format="HH:mm:ss"

                                is-range
                                v-model="chooseDateRange"
                                range-separator="至"
                                start-placeholder="开始时间"
                                end-placeholder="结束时间"
                                placeholder="选择时间范围">
                        </el-time-picker>
                        <el-tooltip style="margin-left: 15px;font-size: 15px" class="item" effect="dark"
                                    content="设置的时段外,端口将不处理请求" placement="right">
                            <i class="el-icon-question"></i>
                        </el-tooltip>
                    </el-form-item>

                    <el-form-item label="备注">
                        <el-input v-model="portMonitoring.name" maxlength="10" autocomplete="off"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button size="mini" @click="closeDateRangeEditDialog">取 消</el-button>
                    <el-button size="mini" type="primary" @click="doDateRangeEdit">确 定</el-button>
                </div>
            </el-dialog>


            <el-dialog title="添加端口监听" :visible.sync="addPortDialog" width="30%" :close-on-click-modal="false">
                <el-form label-position="top" :model="portMonitoring">
                    <el-form-item label="端口">
                        <el-input-number v-model="portMonitoring.port" :min="0" :max="20000"
                                         label=""></el-input-number>
                    </el-form-item>

                    <el-form-item label="接收请求时间段">
                        <el-time-picker
                                value-format="HH:mm:ss"

                                is-range
                                v-model="dateRange"
                                range-separator="至"
                                start-placeholder="开始时间"
                                end-placeholder="结束时间"
                                placeholder="选择时间范围">
                        </el-time-picker>

                        <el-tooltip style="margin-left: 15px;font-size: 15px" class="item" effect="dark"
                                    content="设置的时段外,端口将不处理请求" placement="right">
                            <i class="el-icon-question"></i>
                        </el-tooltip>

                    </el-form-item>

                    <el-form-item label="备注">
                        <el-input v-model="portMonitoring.name" maxlength="10" autocomplete="off"></el-input>
                    </el-form-item>

                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button size="mini" @click="closeAddPortDialog">取 消</el-button>
                    <el-button size="mini" type="primary" @click="createPortMonitoring">确 定</el-button>
                </div>
            </el-dialog>
            <el-dialog title="选择端口关联服务" :visible.sync="portBindDialog" width="45%" :close-on-click-modal="false">
                <el-table :data="serverChannelList" max-height="450">
                    <el-table-column label="服务名称">
                        <template slot-scope="scope"><span :title="scope.row.id" style="text-align: left">{{ scope.row.serviceName }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="服务本地端口">
                        <template slot-scope="scope"><span
                                style="text-align: center">{{ scope.row.servicePort }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="隧道来源">
                        <template slot-scope="scope"><span
                                style="text-align: center">{{ scope.row.routeTo }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button size="mini" type=""
                                       @click="doServiceBind(scope.row)">选 择
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-dialog>
        </el-col>
    </el-row>
</template>

<script>
    import request from "@/config/requestConfig";
    import websocket from "@/config/webSocketTool";

    export default {
        name: "serverPortList",
        data() {
            return {
                chooseDateRange: ['00:00:00', '23:59:59'],
                dateRange: ['00:00:00', '23:59:59'],
                activeName: 'a',
                currentId: '',
                searchKey: '',
                storeArray: [],
                displayArray: [],
                dateRangeEditDialog: false,
                addPortDialog: false,
                portBindDialog: false,
                serverChannelList: [],
                portMonitoring: {
                    enableDateRange: '',
                    name: '',
                    port: ''
                }

            }
        },
        methods: {
            routeToPortListPage(port) {
                this.$router.push({path: '/management/httpApp', query: {port: port}})
            },
            doDateRangeEdit() {
                let enableDateRange = this.chooseDateRange[0] + ',' + this.chooseDateRange[1]
                let body = {
                    enableDateRange: enableDateRange,
                    serverPortId: this.currentId,
                    remark: this.portMonitoring.name
                }
                request({
                    url: '/doDateRangeEdit',
                    method: 'post',
                    data: body
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    if (response.code == 200) {
                        this.$message.success(response.message);
                    } else {
                        this.$message.error(response.message);
                    }
                    this.closeDateRangeEditDialog()
                    this.getServerPortList()
                }).catch(() => {
                })

            },
            closeDateRangeEditDialog() {
                this.dateRangeEditDialog = false
            },
            openDateRangeEditDialog(id, dateRangeStr, remark) {
                this.currentId = id
                this.portMonitoring.name = remark
                this.chooseDateRange = [dateRangeStr.split(",")[0], dateRangeStr.split(",")[1]]
                this.dateRangeEditDialog = true
            },
            checkBefore(d1, d2) {
                return d1.localeCompare(d2) == -1
            },
            checkPortStateByDateRange(dateRange) {
                if (dateRange == '') {
                    dateRange = '00:00:00,23:59:59'
                }
                let dateArray = dateRange.split(",")
                let start = dateArray[0]
                let end = dateArray[1]
                let now = new Date().toLocaleTimeString('it-IT')
                let r1 = this.checkBefore(start, now)
                let r2 = this.checkBefore(now, end)
                if (r1 && r2) {
                    return true
                } else {
                    return false
                }

            },
            clickTab(tab) {
                console.log(tab)
            },
            resetBindRecord(id) {

                let _this = this
                this.$confirm('清除关联记录后,对应服务将不再被自动关联?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let data = {serverPortId: id}
                    request({
                        url: '/resetBindRecord',
                        method: 'post',
                        data: data
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            _this.$message.success(response.message)
                            _this.getServerPortList()
                        } else {
                            _this.$message.error(response.message)
                        }
                        _this.portBindDialog = false
                    }).catch(() => {
                    })

                }).catch(() => {

                });


            },
            deleteServiceBindRecord(id) {
                let _this = this
                this.$confirm('是否删除该端口监听?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {

                    let data = {serverPortId: id}
                    request({
                        url: '/deleteServiceBindRecord',
                        method: 'post',
                        data: data
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            _this.$message.success(response.message);
                            _this.getServerPortList()
                        } else {
                            _this.$message.error(response.message);
                        }
                        _this.portBindDialog = false
                    }).catch(() => {
                    })

                }).catch(() => {

                });


            },
            stopServiceBind(id) {
                let _this = this
                this.$confirm('监听暂停后,已建立连接也将全部断开,是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {

                    let data = {serverPortId: id}
                    request({
                        url: '/stopServiceBind',
                        method: 'post',
                        data: data
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            _this.$message.success(response.message);
                            _this.getServerPortList()
                        } else {
                            _this.$message.error(response.message);
                        }
                        _this.portBindDialog = false
                    }).catch(() => {
                    })

                }).catch(() => {

                });


            },
            doServiceBind(row) {
                let data = {serverPortId: this.currentId, serviceId: row.id}
                request({
                    url: '/doServiceBind',
                    method: 'post',
                    data: data
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    if (response.code == 200) {
                        //refresh force
                        this.$message.success(response.message);
                        this.getServerPortList()
                    } else {
                        this.$message.error(response.message);
                    }
                    this.portBindDialog = false
                }).catch(() => {
                })

            },
            createPortMonitoring() {
                this.portMonitoring.enableDateRange = this.dateRange[0] + ',' + this.dateRange[1]

                request({
                    url: '/createPortMonitoring',
                    method: 'post',
                    data: this.portMonitoring
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    if (response.code == 200) {
                        //refresh force
                        this.storeArray = []
                        this.getServerPortList()
                        this.closeAddPortDialog()
                    } else {
                        this.$message.error(response.message);
                    }


                }).catch(() => {
                })
            },
            closeAddPortDialog() {
                this.addPortDialog = false
                this.portMonitoring = {
                    name: '',
                    port: ''

                }
            },
            openAddPortDialog() {
                this.addPortDialog = true
            },
            openPortBindDialog(id) {
                this.currentId = id
                this.portBindDialog = true
                request({
                    url: '/getServiceList',
                    method: 'post',
                    data: {}
                }).then(response => {
                    this.serverChannelList = response
                }).catch(() => {
                })


            },
            getServerPortList() {
                // eslint-disable-next-line no-constant-condition
                if (true) {
                    // if (this.storeArray.length == 0) {
                    const loading = this.$loading({
                        lock: true,
                        text: 'Loading',
                        spinner: 'el-icon-loading',
                        background: 'rgba(0, 0, 0, 0.7)'
                    })


                    request({
                        url: '/getServerPortList',
                        method: 'post',
                        data: {port: this.searchKey}
                    }).then(response => {
                        this.displayArray = response
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
                        if (x.port.indexOf(this.searchKey) !== -1) {
                            na.push(x)
                        }
                    });
                    this.displayArray = na
                }
            }

        }
        , mounted() {
            if (typeof this.$route.query.port != 'undefined') {
                this.searchKey = this.$route.query.port
            }
            this.getServerPortList()
            websocket.registerPage('serverPortList', '端口监听', this.getServerPortList)
        }
    }
</script>

<style scoped>

</style>
