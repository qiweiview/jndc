<template>
    <el-row style="height: 100vh;">
        <el-col :xs="3" :sm="3" :md="3" :lg="3" :xl="3" style="height: 100%;">
            <el-menu :default-active="$route.path" class="el-menu-vertical-demo" @select="handleSelect">
                <el-menu-item index="/management/channel">
                    <i class="el-icon-s-promotion"></i>
                    <span slot="title">隧道列表</span>
                </el-menu-item>
                <el-menu-item index="/management/services">
                    <i class="el-icon-phone-outline"></i>
                    <span slot="title">服务注册信息</span>
                </el-menu-item>
                <el-menu-item index="/management/serverPortList">
                    <i class="el-icon-camera"></i>
                    <span slot="title">端口监听</span>
                </el-menu-item>
                <el-menu-item index="/management/ipFilter">
                    <i class="el-icon-message-solid"></i>
                    <span slot="title">IP访问管控</span>
                </el-menu-item>
                <el-menu-item index="/management/httpApp">
                    <i class="el-icon-s-comment"></i>
                    <span slot="title">HTTP应用</span>
                </el-menu-item>
                <el-menu-item index="/management/safeExit">
                    <i class="el-icon-close"></i>
                    <span slot="title">安全退出系统</span>
                </el-menu-item>
            </el-menu>
        </el-col>
        <el-col :xs="21" :sm="21" :md="21" :lg="21" :xl="21" style="height: 100%;">
            <router-view></router-view>
        </el-col>
    </el-row>
</template>

<script>
    import websocket from "@/config/webSocketTool";

    export default {
        name: "management",
        data() {
            return {
                hi: 'view'
            }
        },
        methods: {
            openGlobalWebsocket() {
                let ws = websocket.create("GLOBAL", "ws")
                ws.onmessage = (x) => {
                    websocket.parseMessage(x)
                }
                ws.onopen = () => {
                    this.$notify.success({
                        title: '通知',
                        message: '连接推送服务器成功',
                        position: 'bottom-right'
                    });
                }
                ws.onclose = () => {
                    this.$notify.error({
                        title: '通知',
                        message: '推送连接关闭，请刷新页面重新连接',
                        position: 'bottom-right'
                    });
                }
            },
            handleSelect(key) {
                if (key == '/management/safeExit') {
                    localStorage.removeItem('auth-token')
                    this.$router.push('/')
                    this.$message.success("系统登出")
                } else {
                    this.$router.push(key)
                }
            }
        },
        mounted: function () {
            this.openGlobalWebsocket()
            this.$router.push('/management/channel')

        }
    }
</script>

<style scoped>

</style>