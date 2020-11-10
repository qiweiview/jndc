let serverPortTable = {}


serverPortTable.invoke = async function (callBack) {
    let res = await axios.get('./vue_model/serverPortTable.html')
    let route = createRoute(res.data)

    let promise=new Promise((resolve, reject) => {
        resolve(route)
    })

    return promise
}


function createRoute(model) {

    let route = {path: "/serverPortTable"}
    route['component'] = {
        data: function () {
            return {
                array: []
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
                    .catch( (error) =>{
                        this.$parent.$message.error('请求失败:'+error)
                    });
            },
            getRemoteClientList() {

                const loading = this.$loading({
                    lock: true,
                    text: 'Loading',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });


                axios.post('http://localhost:82/getServerMappingList', {}, {
                    auth: static_token
                })
                    .then((response) => {
                        this.array = response.data
                        loading.close()
                    })
                    .catch( (error) => {
                        this.$parent.$message.error('请求失败:'+error)
                        loading.close()
                    });
            }
        },
        mounted() {
        },
        template: model
    }
    return route


}
