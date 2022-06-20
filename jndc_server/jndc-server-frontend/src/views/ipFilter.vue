<template>
    <el-row style="padding: 10px">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <el-tabs v-model="activeName" @tab-click="clickTab">
                <el-tab-pane label="IP黑名单" name="a">
                    <span style="display:block;font-size: 13px;color: #409EFF;padding: 10px">提示：黑名单内IP将被过滤</span>
                    <el-button size="mini" type="success" @click="openAddBlackPage">添 加</el-button>
                    <el-button size="mini" type="info" @click="getIpBlackList">刷 新</el-button>
                    <el-table :data="ipBlackList">
                        <el-table-column label="ip地址">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.ip }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button size="mini" type="danger" @click="deleteRule(scope.row.id,1)">移 除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            @size-change="sizeChange11"
                            @current-change="pageChange11"
                            background
                            :page-sizes="[10, 15, 30, 100]"
                            :page-size="recordRows11"
                            layout="sizes, prev, pager, next"
                            :total="chanelRecordTotal11">
                    </el-pagination>
                </el-tab-pane>
                <el-tab-pane label="IP白名单" name="b">
                    <span style="display: block; font-size: 13px;color: red;padding: 10px">注意：仅白名单内IP允许放行,且优先级高于黑名单，务必谨慎设置</span>
                    <el-button size="mini" type="success" @click="openAddWhitePage">添 加</el-button>
                    <el-button size="mini" type="info" @click="getIpWhiteList">刷 新</el-button>
                    <el-table :data="ipWhiteList">
                        <el-table-column label="ip地址">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.ip }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button size="mini" type="danger" @click="deleteRule(scope.row.id,0)">移 除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            @size-change="sizeChange22"
                            @current-change="pageChange22"
                            background
                            :page-sizes="[10, 15, 30, 100]"
                            :page-size="recordRows22"
                            layout="sizes, prev, pager, next"
                            :total="chanelRecordTotal22">
                    </el-pagination>
                </el-tab-pane>
                <el-tab-pane label="拦截记录" name="c">
                    <el-button size="mini" type="info" @click="getBlockRecord">刷 新</el-button>
                    <el-button size="mini" type="danger" @click="openClearWindow(1)">清 空</el-button>
                    <el-table :data="blockIpList">
                        <el-table-column label="ip地址" sortable prop="ip">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.ip }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="访问次数" sortable prop="count">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.count }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="最后记录时间" sortable prop="lastTimeStamp">
                            <template slot-scope="scope"><span style="text-align: left">{{new Date(scope.row.lastTimeStamp).Format("yyyy-MM-dd HH:mm:ss")  }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button size="mini" type="success" @click="addBlockIpToWhiteList(scope.row.ip)">加入白名单
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            @size-change="sizeChange1"
                            @current-change="pageChange1"
                            background
                            :page-sizes="[10, 15, 30, 100]"
                            :page-size="recordRows1"
                            layout="sizes, prev, pager, next"
                            :total="chanelRecordTotal1">
                    </el-pagination>
                </el-tab-pane>

                <el-tab-pane label="访问记录" name="d">
                    <el-button size="mini" type="info" @click="getReleaseRecord">刷 新</el-button>
                    <el-button size="mini" type="danger" @click="openClearWindow(0)">清 空</el-button>
                    <el-table :data="releaseIpList">
                        <el-table-column label="ip地址" sortable prop="ip">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.ip }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="访问次数" sortable prop="count">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.count }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="最后记录时间" sortable prop="lastTimeStamp">
                            <template slot-scope="scope"><span style="text-align: left">{{new Date(scope.row.lastTimeStamp).Format("yyyy-MM-dd HH:mm:ss")  }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button size="mini" type="danger" @click="addReleaseIpToBlackList(scope.row.ip)">
                                    加入黑名单
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            @size-change="sizeChange2"
                            @current-change="pageChange2"
                            background
                            :page-sizes="[10, 15, 30, 100]"
                            :page-size="recordRows2"
                            layout="sizes, prev, pager, next"
                            :total="chanelRecordTotal2">
                    </el-pagination>
                </el-tab-pane>

                <el-tab-pane label="当前设备IP" name="e">
                    <el-form>
                        <el-form-item label="当前设备网络出口IP：">
                            <span style="font-size: 20px;">{{currentDeviceIp}}</span>
                            <el-button style="margin-left: 15px" @click="getCurrentDeviceIp" size="mini">刷 新</el-button>
                        </el-form-item>
                        <el-form-item label="操作：">
                            <el-button type="success" style="margin-left: 15px" @click="addCurrentDeviceIpToWhiteList"
                                       size="mini">加入白名单
                            </el-button>
                        </el-form-item>
                    </el-form>


                </el-tab-pane>
            </el-tabs>
        </el-col>
        <el-dialog title="添加IP黑名单" :visible.sync="blackAddPage" width="30%">
            <el-form>
                <el-form-item label="IP地址">
                    <el-input style="width: 20%" maxlength="3" v-model="ipBlack.a" autocomplete="off"></el-input>
                    <span>·</span>
                    <el-input style="width: 20%" maxlength="3" v-model="ipBlack.b" autocomplete="off"></el-input>
                    <span>·</span>
                    <el-input style="width: 20%" maxlength="3" v-model="ipBlack.c" autocomplete="off"></el-input>
                    <span>·</span>
                    <el-input style="width: 20%" maxlength="3" v-model="ipBlack.d" autocomplete="off"></el-input>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="closeAddBlackPage">取 消</el-button>
                <el-button size="mini" type="primary" @click="sendAddBlack">确 定</el-button>
            </div>
        </el-dialog>
        <el-dialog title="添加IP白名单" :visible.sync="whiteAddPage" width="30%">
            <el-form>
                <el-form-item label="IP地址">
                    <el-input style="width: 20%" maxlength="3" v-model="ipWhite.a" autocomplete="off"></el-input>
                    <span>·</span>
                    <el-input style="width: 20%" maxlength="3" v-model="ipWhite.b" autocomplete="off"></el-input>
                    <span>·</span>
                    <el-input style="width: 20%" maxlength="3" v-model="ipWhite.c" autocomplete="off"></el-input>
                    <span>·</span>
                    <el-input style="width: 20%" maxlength="3" v-model="ipWhite.d" autocomplete="off"></el-input>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="closeAddWhitePage">取 消</el-button>
                <el-button size="mini" type="primary" @click="sendAddWhite">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="清空记录" :visible.sync="recordClearWindow" width="30%">
            <el-form>
                <el-form-item>
                    <el-radio v-model="clearType" label="1">清空访问量前10以外的记录</el-radio>

                </el-form-item>
                <el-form-item>
                    <el-radio v-model="clearType" label="2">清空选择日期之前的记录</el-radio>
                    <el-date-picker
                            value-format="timestamp"
                            ref="datePicker"
                            v-model="clearDateLimit"
                            type="datetime"
                            placeholder=""
                            align="right"
                            :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>


            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="closeClearWindow">取 消</el-button>
                <el-button size="mini" type="primary" @click="doRecordClear">清 空</el-button>
            </div>
        </el-dialog>
    </el-row>
</template>

<script>
    import request from "@/config/requestConfig";

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
        name: "ipFilter",
        data() {
            return {
                recordType: -1, /* 0 release 1 block*/
                clearDateLimit: '',
                pickerOptions: {
                    shortcuts: [{
                        text: '今天',
                        onClick(picker) {
                            picker.$emit('pick', new Date());
                        }
                    }, {
                        text: '昨天',
                        onClick(picker) {
                            const date = new Date();
                            date.setTime(date.getTime() - 3600 * 1000 * 24);
                            picker.$emit('pick', date);
                        }
                    }, {
                        text: '一周前',
                        onClick(picker) {
                            const date = new Date();
                            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', date);
                        }
                    }]
                },
                clearType: '1',
                recordClearWindow: false,
                currentDeviceIp: '-',
                activeName: 'a',
                ipBlackList: [],
                ipWhiteList: [],
                releaseIpList: [],
                blockIpList: [],
                blackAddPage: false,
                whiteAddPage: false,
                ipBlack: {
                    a: 0,
                    b: 0,
                    c: 0,
                    d: 0,
                },
                ipWhite: {
                    a: 0,
                    b: 0,
                    c: 0,
                    d: 0,
                },
                recordCurrentPage11: 1,
                chanelRecordTotal11: 0,
                recordRows11: 10,
                recordCurrentPage22: 1,
                chanelRecordTotal22: 0,
                recordRows22: 10,
                recordCurrentPage1: 1,
                chanelRecordTotal1: 0,
                recordRows1: 10,
                recordCurrentPage2: 1,
                chanelRecordTotal2: 0,
                recordRows2: 10
            }
        },
        methods: {
            doRecordClear() {
                if (this.clearType === '2') {
                    if (this.clearDateLimit == null || this.clearDateLimit === '') {
                        this.$message.error("请选择日期")
                        this.$refs['datePicker'].focus()
                    }
                }

                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/clearIpRecord',
                    method: 'post',
                    data: {
                        clearType: this.clearType,
                        clearDateLimit: this.clearDateLimit,
                        recordType: this.recordType
                    }
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    this.closeClearWindow()
                    loading.close()
                    if (this.recordType === 0) {
                        this.getReleaseRecord()
                    } else {
                        this.getBlockRecord()
                    }
                }).catch(() => {
                    loading.close()
                })


            },
            closeClearWindow() {
                this.recordClearWindow = false
            },
            openClearWindow(type) {
                this.recordType = type
                this.recordClearWindow = true
            },
            dateFormat() {

            },
            addCurrentDeviceIpToWhiteList() {
                let arr = this.currentDeviceIp.split(".")
                if (arr.length != 4) {
                    this.$message.error("错误的ip")
                }
                this.whiteAddPage = true
                this.ipWhite = {
                    a: arr[0],
                    b: arr[1],
                    c: arr[2],
                    d: arr[3],
                }
                this.activeName = 'b'
            },
            getCurrentDeviceIp() {
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/getCurrentDeviceIp',
                    method: 'post',
                    data: {}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    this.currentDeviceIp = response.ip
                    loading.close()
                }).catch(() => {
                    loading.close()
                })
            },
            sizeChange11(size) {
                this.recordRows1 = size
                this.getIpBlackList()
            },
            pageChange11(page) {
                this.recordCurrentPage1 = page
                this.getIpBlackList()
            },
            sizeChange22(size) {
                this.recordRows2 = size
                this.getIpWhiteList()
            },
            pageChange22(page) {
                this.recordCurrentPage2 = page
                this.getIpWhiteList()
            },
            sizeChange1(size) {
                this.recordRows1 = size
                this.getBlockRecord()
            },
            pageChange1(page) {
                this.recordCurrentPage1 = page
                this.getBlockRecord()
            },
            sizeChange2(size) {
                this.recordRows2 = size
                this.getReleaseRecord()
            },
            pageChange2(page) {
                this.recordCurrentPage2 = page
                this.getReleaseRecord()
            },
            addBlockIpToWhiteList(ip) {
                this.activeName = 'b'
                let arr = ip.split(".")
                if (arr.length != 4) {
                    this.$message.error("错误的ip")
                }
                this.whiteAddPage = true
                this.ipWhite = {
                    a: arr[0],
                    b: arr[1],
                    c: arr[2],
                    d: arr[3],
                }

            },
            addReleaseIpToBlackList(ip) {
                this.activeName = 'a'
                let arr = ip.split(".")
                if (arr.length != 4) {
                    this.$message.error("错误的ip")
                }
                this.blackAddPage = true
                this.ipBlack = {
                    a: arr[0],
                    b: arr[1],
                    c: arr[2],
                    d: arr[3],
                }

            },
            checkIpAddress(ip) {
                let reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
                return reg.test(ip);
            },
            sendAddBlack() {
                let ip = this.ipBlack.a + "." + this.ipBlack.b + "." + this.ipBlack.c + "." + this.ipBlack.d
                if (this.checkIpAddress(ip)) {
                    request({
                        url: '/addToIpBlackList',
                        method: 'post',
                        data: {ip: ip}
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            this.$message.success(response.message)
                            this.getIpBlackList()
                            this.closeAddBlackPage()
                        } else {
                            this.$message.error(response.message)
                        }
                    }).catch(() => {
                    })
                } else {
                    this.$message.error("非法的IP地址：" + ip);
                }
            },
            sendAddWhite() {
                let ip = this.ipWhite.a + "." + this.ipWhite.b + "." + this.ipWhite.c + "." + this.ipWhite.d
                if (this.checkIpAddress(ip)) {
                    request({
                        url: '/addToIpWhiteList',
                        method: 'post',
                        data: {ip: ip}
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            this.closeAddWhitePage()
                            this.$message.success(response.message);
                            this.getIpWhiteList()
                        } else {
                            this.$message.error(response.message);
                        }
                    }).catch(() => {
                    })
                } else {
                    this.$message.error("非法的IP地址：" + ip);
                }
            },
            openAddBlackPage() {
                this.blackAddPage = true
            },
            openAddWhitePage() {
                this.whiteAddPage = true
            },
            closeAddBlackPage() {
                this.blackAddPage = false
                this.ipBlack = {
                    a: 0,
                    b: 0,
                    c: 0,
                    d: 0,
                }
            },
            closeAddWhitePage() {
                this.whiteAddPage = false
                this.ipWhite = {
                    a: 0,
                    b: 0,
                    c: 0,
                    d: 0,
                }
            },
            clickTab(tab) {
                if (tab.name == "a") {
                    this.getIpBlackList()
                }

                if (tab.name == "b") {
                    this.getIpWhiteList()
                }

                if (tab.name == "c") {
                    this.getBlockRecord()
                }

                if (tab.name == "d") {
                    this.getReleaseRecord()
                }

                if (tab.name == "e") {
                    this.getCurrentDeviceIp()
                }


            },
            getIpBlackList() {
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/blackList',
                    method: 'post',
                    data: {page: this.recordCurrentPage11, rows: this.recordRows11}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    this.ipBlackList = response.data
                    this.chanelRecordTotal11 = response.total
                    loading.close()
                }).catch(() => {
                    loading.close()
                })
            },
            getIpWhiteList() {
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/whiteList',
                    method: 'post',
                    data: {page: this.recordCurrentPage22, rows: this.recordRows22}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    this.ipWhiteList = response.data
                    this.chanelRecordTotal22 = response.total
                    loading.close()
                }).catch(() => {
                    loading.close()
                })
            },
            getReleaseRecord() {
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/releaseRecord',
                    method: 'post',
                    data: {page: this.recordCurrentPage2, rows: this.recordRows2}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    this.releaseIpList = response.data
                    this.chanelRecordTotal2 = response.total
                    loading.close()
                }).catch(() => {
                    loading.close()
                })
            },
            getBlockRecord() {
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/blockRecord',
                    method: 'post',
                    data: {page: this.recordCurrentPage1, rows: this.recordRows1}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    this.blockIpList = response.data
                    this.chanelRecordTotal1 = response.total
                    loading.close()
                }).catch(() => {
                    loading.close()
                })
            },
            deleteRule(id, tag) {
                request({
                    url: '/deleteIpRuleByPrimaryKey',
                    method: 'post',
                    data: {id: id}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    if (response.code == 200) {
                        //refresh force
                        this.$message.success(response.message)
                        if (tag == 0) {
                            this.getIpWhiteList()
                        } else {
                            this.getIpBlackList()
                        }

                    } else {
                        this.$message.error(response.message)
                    }
                }).catch(() => {
                })
            }
        },
        mounted: function () {
            this.getIpBlackList()
        }
    }
</script>

<style scoped>

</style>
