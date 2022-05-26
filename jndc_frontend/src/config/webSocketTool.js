import {Notification} from 'element-ui'

let websocket4v = {}
let store = {}

// eslint-disable-next-line no-unused-vars
let currentPage

let pageEventArray = []

websocket4v.registerPage = (page, pageDescription, callBack) => {
    pageEventArray.push({'pageName': page, 'pageDescription': pageDescription, 'callBack': callBack})
}

websocket4v.parseMessage = (x) => {
    let obj = JSON.parse(x)
    if (1 == obj.type) {
        //refresh page
        pageEventArray.forEach(x => {
            if (x.pageName == obj.data) {
                x.callBack()
                console.log(x.pageDescription + '数据刷新')
                // Notification.success({
                //     title: '通知',
                //     message: x.pageDescription+'数据刷新',
                //     position: 'bottom-right'
                // })
            }
        })
    } else {
        console.log("数据", x);
        Notification.info({
            title: '通知',
            message: obj.data,
            position: 'bottom-right'
        })
    }
}


websocket4v.find = (name) => {
    return store[name]
}


websocket4v.create = (name, url) => {
    if (typeof (store[name]) != 'undefined') {
        console.error('the object:' + name + ' exist,the older socket has been covered')
    }


    let singleWebSocketHolder = {'name': name}
    singleWebSocketHolder.onmessage = (x) => {
        console.log('default action: receive ' + x)
    }
    singleWebSocketHolder.onopen = () => {
        console.log('default action: open ws ')
    }
    singleWebSocketHolder.onclose = () => {
        console.log('default action: close ws ')
    }


    let wsURL = "ws://" + window.location.host + '/' + url + '?auth-token=' + localStorage.getItem('auth-token')
    console.log(wsURL, wsURL)

    let inner = new WebSocket(wsURL);


    inner.onopen = () => {
        try {
            singleWebSocketHolder.onopen()
        } catch (e) {
            console.error('can not found the method onopen() by singleWebSocketHolder')
        }
    };

    inner.onmessage = (evt) => {
        let received_msg = evt.data
        try {
            singleWebSocketHolder.onmessage(received_msg)
        } catch (e) {
            console.error(e)
        }
    };


    inner.onclose = () => {
        try {
            singleWebSocketHolder.onclose()
        } catch (e) {
            console.error('can not found the method onclose() by singleWebSocketHolder')
        }
    };
    singleWebSocketHolder['object'] = inner

    store[name] = singleWebSocketHolder

    return singleWebSocketHolder
}

export default websocket4v
