import axios from 'axios'
import {Message} from 'element-ui'


// create an axios instance
const service = axios.create({
  baseURL: window.runtimeConfig.BASE_REQUEST_PATH, // url = base url + request url
  timeout: 15 * 1000 // request timeout
})


// request interceptor
service.interceptors.request.use(
    config => {
      config.headers['auth-token'] = localStorage.getItem('auth-token')
      config.headers['Content-Type'] = 'application/json;charset=UTF-8';
      return config
    },
    error => {
      console.log(error) // for debug
      return Promise.reject(error)
    }
)

// response interceptor
service.interceptors.response.use(
    /**
     * If you want to get http information such as headers or status
     * Please return  response => response
     */

    /**
     * Determine the request status by custom code
     * Here is just an example
     * You can also judge the status by HTTP Status Code
     */
    response => {
      const res = response.data
      return res
    },
    error => {
      if (error.message.indexOf('403') != -1) {
        localStorage.removeItem('auth-token')
        window.location.reload()
      }

      Message({
        message: error.message,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error)
    }
)

export default service
