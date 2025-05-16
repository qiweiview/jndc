// vue.config.js
module.exports = {
    //assetsDir: 'static',
    outputDir: 'compare_dist',
    devServer: {
        port: 778,
        proxy: {
            '/': {
                target: 'https://qw607.com:4477',  // 接口域名
                secure: false,  // 如果是https接口，需要配置这个参数
                changeOrigin: true,  //是否跨域
            }
        }
    }
}
