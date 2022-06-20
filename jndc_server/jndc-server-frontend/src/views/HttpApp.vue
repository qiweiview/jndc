<template>
    <el-row style="padding: 10px">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <span style="display:block;font-size: 13px;color: #409EFF;padding: 10px">提示：规则生效于服务端配置的web端口</span>

            <el-input clearable v-model="searchKey" placeholder="筛选域名规则"
                      @change="getHostList" style="width:20%"></el-input>
            <el-button size="mini" @click="getHostList" style="margin-left:15px">查 询</el-button>
            <el-button size="mini" @click="openHostCreateBlog" style="margin-left:15px" type="success">新 增</el-button>
            <el-table :data="hostList" max-height="700px">
                <el-table-column label="域名规则字符">
                    <template slot-scope="scope"><span style="text-align: left">{{ scope.row.hostKeyWord }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="路由操作">
                    <template slot-scope="scope"><span
                            style="text-align: left">{{ routeTypeCN(scope.row.routeType)}}</span>
                    </template>
                </el-table-column>
                <el-table-column label="转发端口">
                    <template slot-scope="scope"><span
                            @click="routeToPortListPage(scope.row.forwardPort)"
                            :style="{'text-align': 'left','color': scope.row.forwardPort==0?'black':'deepskyblue','cursor': scope.row.forwardPort==0?'-':'pointer'}">{{ scope.row.forwardPort==0?'-':scope.row.forwardPort}}</span>
                    </template>
                </el-table-column>

                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-button size="mini" type="info" @click="editHostConfig(scope.row)">编 辑</el-button>
                        <el-button size="mini" type="danger" @click="deleteHostRoute(scope.row)">删 除</el-button>
                        <el-button size="mini" type="success" @click="openRouteAddress(scope.row)">访 问</el-button>
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
        </el-col>

        <!--=====================新增=============-->
        <el-dialog top="3vh" :close-on-press-escape="false" :close-on-click-modal="false" title="新增域名规则"
                   :visible.sync="hostCreateBlog" width="55%">
            <el-form label-position="top">
                <el-form-item label="域名包含字符">
                    <span style="display:block;font-size: 13px;color: #409EFF;padding: 10px">
                         提示：规则将匹配类似{{hostForm.hostKeyWord}}.abc.com或{{hostForm.hostKeyWord}}.abc.cn等域名
                    </span>
                    <el-input ref="routeKey" style="width: 60%" v-model="hostForm.hostKeyWord"></el-input>
                </el-form-item>

                <el-form-item label="该规则下,使用固定返回值">
                    <el-radio v-model="hostForm.routeType" @change="focusForward" label="2">请求转发</el-radio>
                    <el-radio v-model="hostForm.routeType" label="0">重定向</el-radio>
                    <el-radio v-model="hostForm.routeType" label="1">固定值返回</el-radio>
                </el-form-item>

                <el-form-item label="消息类型" v-show="hostForm.routeType=='1'">
                    <el-select v-model="hostForm.contentType" placeholder="请选择" @change="changeDefaultContent">
                        <el-option
                                v-for="item in options"
                                :key="item.value"
                                :label="item.value"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="固定返回值" v-show="hostForm.routeType=='1'">
                    <editor :options="{ maxLines: 13,autoScrollEditorIntoView: true}" v-model="hostForm.fixedTextArea"
                            @init="editorInit" :lang="mapLang(hostForm.contentType)"
                            theme="chrome" width="100%" height="200px"></editor>
                </el-form-item>

                <el-form-item label="重定向地址" v-show="hostForm.routeType=='0'">
                    <el-select v-model="hostForm.forwardProtocol" placeholder="请选择" style="width: 15%">
                        <el-option

                                v-for="item in protocols"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                    <el-input style="width: 60%;margin-left: 5px" v-model="hostForm.redirectAddress"></el-input>
                    <el-button size="mini" type="primary" style="margin-left: 15px"
                               @click="openInNewWindow(hostForm.forwardProtocol+hostForm.redirectAddress)">测试
                    </el-button>
                </el-form-item>

                <el-form-item label="转发目标" v-show="hostForm.routeType=='2'">
                    <span>转发至端口：{{ this.hostForm.forwardPort==0?'未选择':this.hostForm.forwardPort}}</span>
                    <el-tooltip class="item" effect="dark" content="目标来源于 '端口监听' 模块" placement="right">
                        <i style="color: gray;margin-left: 5px;font-size: 15px;" class="el-icon-question"></i>
                    </el-tooltip>
                    <el-table max-height="350px" :data="displayArray" style="margin: 0">
                        <el-table-column label="监听端口" width="100px">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.port }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="备注" width="300px">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.name }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="服务关联记录" width="200px">
                            <template slot-scope="scope"><span
                                    style="">{{ scope.row.routeTo==null?'未关联过服务':scope.row.routeTo}}</span></template>
                        </el-table-column>


                        <el-table-column fixed="right" label="操作" width="100px">

                            <template slot-scope="scope">
                                <el-button size="mini" type="primary" @click="chooseService(scope.row)">
                                    选择
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="closeHostCreateBlog">取 消</el-button>
                <el-button size="mini" type="primary" @click="doHostCreate">新 增</el-button>
            </div>
        </el-dialog>

        <!-- ================== 编辑 ====================================-->
        <el-dialog top="3vh" :close-on-press-escape="false" :close-on-click-modal="false" title="编辑域名规则"
                   :visible.sync="hostCreateBlogEdit" width="65%">
            <el-form label-position="top">
                <el-form-item label="域名包含字符">
                     <span style="display:block;font-size: 13px;color: #409EFF;padding: 10px">
                        提示：规则将匹配类似{{hostFormEdit.hostKeyWord}}.abc.com或{{hostFormEdit.hostKeyWord}}.abc.cn等域名
                    </span>
                    <el-input style="width: 60%" v-model="hostFormEdit.hostKeyWord"></el-input>
                </el-form-item>

                <el-form-item label="该规则下,使用固定返回值">
                    <el-radio v-model="hostFormEdit.routeType" @change="focusForward" label="2">请求转发</el-radio>
                    <el-radio v-model="hostFormEdit.routeType" label="0">重定向</el-radio>
                    <el-radio v-model="hostFormEdit.routeType" label="1">固定值返回</el-radio>
                </el-form-item>

                <el-form-item label="消息类型" v-show="hostFormEdit.routeType=='1'">
                    <el-select v-model="hostFormEdit.contentType" placeholder="请选择">
                        <el-option
                                v-for="item in options"
                                :key="item.value"
                                :label="item.value"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="固定返回值" v-show="hostFormEdit.routeType=='1'">
                    <editor :options="{ showPrintMargin: false,maxLines: 13,autoScrollEditorIntoView: true}"
                            v-model="hostFormEdit.fixedTextArea" @init="editorInit"
                            :lang="mapLang(hostFormEdit.contentType)"
                            theme="chrome" width="100%" height="200px"></editor>
                </el-form-item>

                <el-form-item label="重定向地址" v-show="hostFormEdit.routeType=='0'">
                    <el-select v-model="hostFormEdit.forwardProtocol" placeholder="请选择" style="width: 15%">
                        <el-option

                                v-for="item in protocols"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                    <el-input style="width: 60%" v-model="hostFormEdit.redirectAddress"></el-input>
                    <el-button size="mini" type="primary" style="margin-left: 15px"
                               @click="openInNewWindow(hostFormEdit.forwardProtocol+hostFormEdit.redirectAddress)">测试
                    </el-button>
                </el-form-item>

                <el-form-item label="转发目标" v-show="hostFormEdit.routeType=='2'">
                    <span>转发至端口：{{ this.hostFormEdit.forwardPort==0?'未选择':this.hostFormEdit.forwardPort}}</span>
                    <el-tooltip class="item" effect="dark" content="目标来源于 '端口监听' 模块" placement="right">
                        <i style="color: gray;margin-left: 5px;font-size: 15px;" class="el-icon-question"></i>
                    </el-tooltip>
                    <el-table :data="displayArray" style="margin: 0" max-height="350px">
                        <el-table-column label="监听端口" width="100px">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.port }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="备注" width="400px">
                            <template slot-scope="scope"><span style="text-align: left">{{ scope.row.name }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="服务关联记录" width="400px">
                            <template slot-scope="scope"><span
                                    style="">{{ scope.row.routeTo==null?'未关联过服务':scope.row.routeTo}}</span></template>
                        </el-table-column>
                        <el-table-column fixed="right" label="操作" width="100px">

                            <template slot-scope="scope">
                                <el-button size="mini" type="primary" @click="chooseServiceForEdit(scope.row)">
                                    选择
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button size="mini" @click="closeHostConfigDialog">取 消</el-button>
                <el-button size="mini" type="primary" @click="doHostUpdate">更 新</el-button>
            </div>
        </el-dialog>


    </el-row>
</template>

<script>
    import request from "@/config/requestConfig";

    export default {
        components: {
            editor: require('vue2-ace-editor')
        },
        name: "serviceList",
        data() {
            let htmlContent =
                '<html>\n' +
                '<head>\n' +
                '<meta charset="UTF-8" />\n' +
                '<link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css" />\n' +
                '</head> \n' +
                ' <body>\n' +
                '  <div id="app">\n' +
                '     {{world}}\n' +
                '  </div> \n' +
                '<script src="https://unpkg.com/vue/dist/vue.js"><scriptEnd>\n' +
                '<script src="https://unpkg.com/element-ui/lib/index.js"><scriptEnd>\n' +
                '<script>\n' +
                ' new Vue({\n' +
                " el: '#app',\n" +
                'data: function() {\n' +
                '      return { hello: "world" }\n' +
                '                 }\n' +
                ' })\n' +
                '<scriptEnd>\n' +
                '</body>\n' +
                '</html>'

            htmlContent = htmlContent.replace(new RegExp('scriptEnd', 'g'), "/script")

            return {
                protocols: [{
                    value: 'http://',
                    label: 'http://'
                }, {
                    value: 'https://',
                    label: 'https://'
                }],
                displayArray: [],
                searchKey: '',
                hostList: [],
                recordRows: 15,
                recordCurrentPage: 1,
                chanelRecordTotal: 0,
                hostCreateBlog: false,
                hostCreateBlogEdit: false,
                currentHost: "",
                hostForm: {
                    hostKeyWord: '',
                    routeType: '2',
                    fixedTextArea: '',
                    contentType: 'application/json',
                    redirectAddress: '',
                    forwardHost: '',
                    forwardPort: 0,
                    forwardProtocol: 'http://'
                },
                hostFormEdit: {
                    id: '',
                    hostKeyWord: '',
                    routeType: '2',
                    fixedTextArea: '',
                    contentType: 'application/json',
                    redirectAddress: '',
                    forwardHost: '',
                    forwardPort: 0,
                    forwardProtocol: 'http://'
                },
                options: [
                    {
                        defaultContent: '{"name":"hello world"}',
                        value: 'application/json'
                    },
                    {
                        defaultContent: '<key>123</key>',
                        value: 'application/xml'
                    },
                    {
                        defaultContent: 'let age=18',
                        value: 'application/javascript'
                    },
                    {
                        defaultContent: 'hello world',
                        value: 'text/plain'
                    },
                    {
                        defaultContent: htmlContent,
                        value: 'text/html'
                    }
                ]
            }
        },
        methods: {
            routeToPortListPage(port) {
                this.$router.push({path: '/management/serverPortList', query: {port: port}})
            },
            changeDefaultContent(e) {
                this.options.forEach(x => {
                    if (e == x.value) {
                        this.hostForm.fixedTextArea = x.defaultContent
                    }
                })
            },
            mapLang(type) {
                if (type == 'application/json') {
                    return 'json'
                } else if (type == 'text/html') {
                    return 'html'
                } else if (type == 'application/xml') {
                    return 'xml'
                } else if (type == 'application/xml') {
                    return 'javascript'
                } else {
                    return 'text'
                }
            },
            editorInit() {
                require('brace/ext/language_tools') //language extension prerequsite...
                require('brace/theme/twilight')
                require('brace/snippets/javascript') //snippet

                require('brace/mode/html')
                require('brace/mode/text')
                require('brace/mode/javascript')    //language
                require('brace/mode/xml')
                require('brace/mode/json')


            },
            openRouteAddress(x) {
                let host = window.location.host
                let end = host.indexOf(":")
                let nHost = window.location.protocol + '//' + x.hostKeyWord + "." + host.substring(0, end)
                window.open(nHost)
            },
            routeTypeCN(code) {
                if (code == 0) {
                    return '重定向'
                }
                if (code == 1) {
                    return '固定值'
                }
                if (code == 2) {
                    return '转发'
                }
                return '未知'
            },
            chooseService(x) {
                this.hostForm.forwardPort = x.port
                this.doHostCreate()
            },
            chooseServiceForEdit(x) {
                this.hostFormEdit.forwardPort = x.port
                this.doHostUpdate()
            },
            focusForward(x) {
                console.log(x);
                this.getServiceList()
            },
            getServiceList() {
                // eslint-disable-next-line no-constant-condition

                request({
                    url: '/getServerPortList',
                    method: 'post',
                    data: {}
                }).then(response => {
                    this.displayArray = response

                }).catch(() => {
                })


            },
            openInNewWindow(url) {
                console.log(url);
                window.open(url)
            },
            deleteHostRoute(row) {

                let _this = this
                this.$confirm('删除该配置，是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    request({
                        url: '/deleteHostRouteRule',
                        method: 'post',
                        data: {id: row.id}
                        // eslint-disable-next-line no-unused-vars
                    }).then(response => {
                        if (response.code == 200) {
                            //refresh force
                            _this.$message.success(response.message)
                            _this.getHostList()
                        } else {
                            _this.$message.error(response.message)
                        }
                    }).catch(() => {
                    })


                }).catch(() => {

                });
            },
            doHostCreate() {
                let body = {
                    hostKeyWord: this.hostForm.hostKeyWord,
                    routeType: this.hostForm.routeType,
                    fixedResponse: this.hostForm.fixedTextArea,
                    redirectAddress: this.hostForm.redirectAddress,
                    fixedContentType: this.hostForm.contentType,
                    forwardHost: this.hostForm.forwardHost,
                    forwardPort: this.hostForm.forwardPort,
                    forwardProtocol: this.hostForm.forwardProtocol
                }
                if ('' == body.hostKeyWord) {
                    this.$message.error('关键字不能为空')
                    this.$refs['routeKey'].focus()
                    return
                }

                if (body.routeType == 2 && 0 == body.forwardPort) {
                    this.$message.error('未选择转发至端口')
                    return
                }
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/saveHostRouteRule',
                    method: 'post',
                    data: body
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    loading.close()
                    if (response.code == 200) {
                        //refresh force
                        this.$message.success(response.message)
                        this.getHostList()
                        this.closeHostCreateBlog()
                    } else {
                        this.$message.error(response.message)
                    }

                }).catch(() => {
                    loading.close()
                })
            },
            doHostUpdate() {
                let body = {
                    id: this.hostFormEdit.id,
                    hostKeyWord: this.hostFormEdit.hostKeyWord,
                    routeType: this.hostFormEdit.routeType,
                    fixedResponse: this.hostFormEdit.fixedTextArea,
                    redirectAddress: this.hostFormEdit.redirectAddress,
                    fixedContentType: this.hostFormEdit.contentType,
                    forwardHost: this.hostFormEdit.forwardHost,
                    forwardPort: this.hostFormEdit.forwardPort,
                    forwardProtocol: this.hostFormEdit.forwardProtocol
                }

                if ('' == body.hostKeyWord) {
                    this.$message.error('关键字不能为空')
                    this.$refs['routeKey'].focus()
                    return
                }

                if (body.routeType == 2 && 0 == body.forwardPort) {
                    this.$message.error('未选择转发至端口')
                    return
                }

                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/updateHostRouteRule',
                    method: 'post',
                    data: body
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    loading.close()
                    if (response.code == 200) {
                        //refresh force
                        this.$message.success(response.message)
                        this.getHostList()
                        this.closeHostConfigDialog()
                    } else {
                        this.$message.error(response.message)
                    }

                }).catch(() => {
                    loading.close()
                })
            },
            closeHostCreateBlog() {
                this.hostCreateBlog = false
            },
            openHostCreateBlog() {
                this.hostForm = {
                    hostKeyWord: '',
                    routeType: '2',
                    fixedTextArea: '',
                    contentType: 'application/json',
                    redirectAddress: '',
                    forwardHost: '',
                    forwardPort: 0,
                    forwardProtocol: 'http://'
                }
                this.hostCreateBlog = true
            },
            pageChange(page) {
                this.recordCurrentPage = page
                this.getHostList()
            },
            sizeChange(size) {
                this.recordRows = size
                this.getHostList()
            },
            getHostList() {
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })
                request({
                    url: '/listHostRouteRule',
                    method: 'post',
                    data: {page: this.recordCurrentPage, rows: this.recordRows, hostKeyWord: this.searchKey}
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {
                    this.hostList = response.data
                    this.chanelRecordTotal = response.total
                    loading.close()
                }).catch(() => {
                    loading.close()
                })
            },
            editHostConfig(row) {

                this.hostFormEdit = {
                    id: row.id,
                    hostKeyWord: row.hostKeyWord,
                    routeType: row.routeType + '',
                    fixedTextArea: row.fixedResponse,
                    contentType: row.fixedContentType,
                    redirectAddress: row.redirectAddress,
                    forwardHost: row.forwardHost,
                    forwardPort: row.forwardPort,
                    forwardProtocol: row.forwardProtocol
                }
                this.hostCreateBlogEdit = true
            },
            closeHostConfigDialog() {
                this.hostFormEdit = {
                    id: '',
                    hostKeyWord: '',
                    routeType: '2',
                    fixedTextArea: '',
                    contentType: 'application/json',
                    redirectAddress: '',
                    forwardHost: '',
                    forwardPort: 0,
                    forwardProtocol: 'http://'
                }
                this.hostCreateBlogEdit = false
            }

        }, mounted() {
            if (typeof this.$route.query.port != 'undefined') {
                this.searchKey = this.$route.query.port
            }
            this.currentHost = window.runtimeConfig.BASE_REQUEST_PATH
            this.getHostList()
            this.getServiceList()
        }
    }
</script>

<style scoped>

</style>
