let serverChannelTable = {}


serverChannelTable.invoke = async function (callBack) {
    let res = await axios.get('./vue_model/serverChannelTable.html')
    let route = serverChannelTableRoute(res.data)

    let promise = new Promise((resolve, reject) => {
        resolve(route)
    })

    return promise
}


function serverChannelTableRoute(model) {

    let route = {path: "/serverChannelTable"}
    route['component'] = {
        data: function () {
            return {
                array: [],
                portArray: [],
                portSelect: ''
            }
        },
        methods: {
            closeChannelByServer(channelId, ports) {

                let _this = this


                this.$confirm('断开隧道后,与隧道相关联的端口："' + ports + '" 将不再监听，已建立的连接都将断开，是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    axios.post('http://localhost:82/closeChannelByServer', {
                        channelId: channelId
                    }, {
                        auth: static_token
                    })
                        .then((response) => {
                            _this.getServerChannelTable()
                        })
                        .catch((error) => {
                            _this.$parent.$message.error('请求失败:' + error)
                        });
                }).catch(() => {

                });


            },
            getServerChannelTable() {

                const loading = this.$loading({
                    lock: true,
                    text: 'Loading',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })


                axios.post('http://localhost:82/getServerChannelTable', {serverPort: this.portSelect}, {
                    auth_token: static_token
                })
                    .then((response) => {
                        this.array = response.data
                        loading.close()
                    })
                    .catch((error) => {
                        this.$parent.$message.error('请求失败:' + error)
                        loading.close()
                    });
            },
            getServerPortList() {

                axios.post('http://localhost:82/getServerPortList', {}, {
                    auth: static_token
                })
                    .then((response) => {
                        this.portArray = response.data
                    })
                    .catch((error) => {
                        this.$parent.$message.error('请求失败:' + error)
                    });
            }
        },
        mounted() {
            this.getServerChannelTable()
        },
        template: model
    }
    return route


}
