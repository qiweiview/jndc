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
                portSelect:''
            }
        },
        methods: {
            shutDown(serverPort) {
                axios.post('http://localhost:82/shutDownServerPort', {
                    port: serverPort
                }, {
                    auth: static_token
                })
                    .then((response) => {
                        this.getRemoteClientList()
                    })
                    .catch( (error) => {
                        this.$parent.$message.error('请求失败:'+error)
                    });
            },
            getServerChannelTable() {

                const loading = this.$loading({
                    lock: true,
                    text: 'Loading',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })


                axios.post('http://localhost:82/getServerChannelTable', {serverPort:this.portSelect}, {
                    auth: static_token
                })
                    .then((response) => {
                        this.array = response.data
                        loading.close()
                    })
                    .catch( (error) =>{
                        this.$parent.$message.error('请求失败:'+error)
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
                    .catch( (error) =>{
                        this.$parent.$message.error('请求失败:'+error)
                    });
            }
        },
        mounted() {
            this.getServerPortList()
        },
        template: model
    }
    return route


}
