<template>
    <div class="login-container"
         style="background-image: url('https://s1.ax1x.com/2020/11/09/BHG0gg.jpg');width: 100vw;height: 100vh">
        <el-row style="height: 100vh;">
            <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24" class="parent" style="height: 100%;">
                <el-form ref="form" :model="form" label-width="80px" class="login-form" style="width:30vw">
                    <el-form-item>
                        <h2 style="text-align: center;font-size: 55px;color: white;">J N D C - Admin</h2>
                    </el-form-item>
                    <el-form-item label="用户名">
                        <el-input v-model="form.name" placeholder="用户名"></el-input>
                    </el-form-item>
                    <el-form-item label="密码">
                        <el-input @keyup.native.enter="doLogin" v-model="form.passWord" type="password" ref="password"
                                  placeholder="密码"></el-input>
                    </el-form-item>
                    <el-form-item style="text-align: right;">
                        <el-button size="mini" type="primary" @click="doLogin">登录</el-button>
                        <el-button size="mini">取消</el-button>
                    </el-form-item>
                </el-form>
            </el-col>

        </el-row>
    </div>
</template>

<script>
    import request from '@/config/requestConfig'

    export default {
        name: "login",
        data() {
            return {
                form: {
                    name: '',
                    passWord: ''
                }
            }
        },
        methods: {
            doLogin() {
                const loading = this.$loading({
                    lock: true,
                    text: '登陆中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                })

                request({
                    url: '/login',
                    method: 'post',
                    data: this.form
                }).then(response => {
                    loading.close()
                    if (response.token == '403') {
                        this.$message.error('密码错误');
                    } else {
                        localStorage.setItem('auth-token', response.token)
                        this.$message.success('登录成功');

                        this.$router.push('management')
                    }
                }).catch(() => {
                    loading.close()
                })
            }
        }
    }
</script>


<style scoped>


    .login-form {
        position: relative;
        width: 520px;
        max-width: 100%;
        padding: 160px 35px 0;
        margin: 0 auto;
        overflow: hidden;
    }

    .tips {
        font-size: 14px;
        color: #fff;
        margin-bottom: 10px;


    }

    .svg-container {
        padding: 6px 5px 6px 15px;
        color: #889aa4;
        vertical-align: middle;
        width: 30px;
        display: inline-block;
    }

    .title-container {
        position: relative;

    .title {
        font-size: 26px;
        color: #eee;
        margin: 0px auto 40px auto;
        text-align: center;
        font-weight: bold;
    }

    }

    .show-pwd {
        position: absolute;
        right: 10px;
        top: 7px;
        font-size: 16px;
        color: #889aa4;
        cursor: pointer;
        user-select: none;
    }

</style>
