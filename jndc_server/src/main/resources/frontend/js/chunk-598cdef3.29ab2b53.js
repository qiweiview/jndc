(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-598cdef3"],{"0aef":function(e,t,o){"use strict";o("4160");var n=o("5c96"),s={},a={},i=[];s.registerPage=function(e,t,o){i.push({pageName:e,pageDescription:t,callBack:o})},s.parseMessage=function(e){var t=JSON.parse(e);1==t.type?i.forEach((function(e){e.pageName==t.data&&(e.callBack(),console.log(e.pageDescription+"数据刷新"))})):(console.log("数据",e),n["Notification"].info({title:"通知",message:t.data,position:"bottom-right"}))},s.find=function(e){return a[e]},s.create=function(e,t){"undefined"!=typeof a[e]&&console.error("the object:"+e+" exist,the older socket has been covered");var o={name:e,onmessage:function(e){console.log("default action: receive "+e)},onopen:function(){console.log("default action: open ws ")},onclose:function(){console.log("default action: close ws ")}},n="ws://"+window.location.host+"/"+t+"?auth-token="+localStorage.getItem("auth-token");console.log(n,n);var s=new WebSocket(n);return s.onopen=function(){try{o.onopen()}catch(e){console.error("can not found the method onopen() by singleWebSocketHolder")}},s.onmessage=function(e){var t=e.data;try{o.onmessage(t)}catch(n){console.error(n)}},s.onclose=function(){try{o.onclose()}catch(e){console.error("can not found the method onclose() by singleWebSocketHolder")}},o["object"]=s,a[e]=o,o},t["a"]=s},f15f:function(e,t,o){"use strict";o.r(t);var n=function(){var e=this,t=e.$createElement,o=e._self._c||t;return o("el-row",{staticStyle:{height:"100vh"}},[o("el-col",{staticStyle:{height:"100%"},attrs:{xs:3,sm:3,md:3,lg:3,xl:3}},[o("el-menu",{staticClass:"el-menu-vertical-demo",attrs:{"default-active":e.$route.path},on:{select:e.handleSelect}},[o("el-menu-item",{attrs:{index:"/management/channel"}},[o("i",{staticClass:"el-icon-s-promotion"}),o("span",{attrs:{slot:"title"},slot:"title"},[e._v("隧道列表")])]),o("el-menu-item",{attrs:{index:"/management/services"}},[o("i",{staticClass:"el-icon-phone-outline"}),o("span",{attrs:{slot:"title"},slot:"title"},[e._v("服务注册信息")])]),o("el-menu-item",{attrs:{index:"/management/serverPortList"}},[o("i",{staticClass:"el-icon-camera"}),o("span",{attrs:{slot:"title"},slot:"title"},[e._v("端口监听")])]),o("el-menu-item",{attrs:{index:"/management/ipFilter"}},[o("i",{staticClass:"el-icon-message-solid"}),o("span",{attrs:{slot:"title"},slot:"title"},[e._v("IP访问管控")])]),o("el-menu-item",{attrs:{index:"/management/httpApp"}},[o("i",{staticClass:"el-icon-s-comment"}),o("span",{attrs:{slot:"title"},slot:"title"},[e._v("HTTP应用")])]),o("el-menu-item",{attrs:{index:"/management/safeExit"}},[o("i",{staticClass:"el-icon-close"}),o("span",{attrs:{slot:"title"},slot:"title"},[e._v("安全退出系统")])])],1)],1),o("el-col",{staticStyle:{height:"100%"},attrs:{xs:21,sm:21,md:21,lg:21,xl:21}},[o("router-view")],1)],1)},s=[],a=o("0aef"),i={name:"management",data:function(){return{hi:"view"}},methods:{openGlobalWebsocket:function(){var e=this,t=a["a"].create("GLOBAL","ws");t.onmessage=function(e){a["a"].parseMessage(e)},t.onopen=function(){e.$notify.success({title:"通知",message:"连接推送服务器成功",position:"bottom-right"})},t.onclose=function(){e.$notify.error({title:"通知",message:"推送连接关闭，请刷新页面重新连接",position:"bottom-right"})}},handleSelect:function(e){"/management/safeExit"==e?(localStorage.removeItem("auth-token"),this.$router.push("/"),this.$message.success("系统登出")):this.$router.push(e)}},mounted:function(){this.openGlobalWebsocket(),this.$router.push("/management/channel")}},l=i,c=o("2877"),r=Object(c["a"])(l,n,s,!1,null,"f0311762",null);t["default"]=r.exports}}]);
//# sourceMappingURL=chunk-598cdef3.29ab2b53.js.map