import Vue from 'vue'
import App from './App.vue'
import router from "@/config/routeConfig";
import '@/config/uiConfig'

Vue.config.productionTip = false


new Vue({
  render: h => h(App),
  router
}).$mount('#app')
