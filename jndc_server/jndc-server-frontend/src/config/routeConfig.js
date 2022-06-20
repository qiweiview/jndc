import Vue from 'vue'
import Router from 'vue-router'
import {Message} from 'element-ui'

Vue.use(Router)

const originalPush = Router.prototype.push

Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

const router = new Router({

    routes: [
        {
            path: '*',
            component: () => import('@/views/resourceNotFound')
        },
        {
            path: '/',
            component: () => import('@/views/login')
        },
        {
            path: '/dag',
            component: () => import('@/views/dag')
        },
        {
            path: '/management',
            component: () => import('@/views/management'),
            children: [
                {
                    path: 'channel',
                    component: () => import('@/views/channelList')
                },
                {
                    path: 'services',
                    component: () => import('@/views/serviceList')
                },
                {
                    path: 'serverPortList',
                    component: () => import('@/views/serverPortList')
                },
                {
                    path: 'ipFilter',
                    component: () => import('@/views/ipFilter')
                },
                {
                    path: 'httpApp',
                    component: () => import('@/views/HttpApp')
                }
            ]

        }
    ]
})

let unAuthUrl = ['/', '/login', '/rtc', '/dag']

function isInUnAuthList(url) {
    let rs = false
    unAuthUrl.forEach(x => {
        if (url == x) {
            rs = true
        }
    })
    return rs
}

router.beforeEach((to, from, next) => {
    if (isInUnAuthList(to.path)) {
        next()
    } else {
        let auth = localStorage.getItem('auth-token')
        if (null == auth) {
            Message.error('凭证过期，请重新登录');
            next({path: '/'})
        } else {
            next()
        }
    }
})

export default router
