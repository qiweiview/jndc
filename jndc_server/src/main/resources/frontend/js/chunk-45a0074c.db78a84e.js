(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-45a0074c"],{bfa7:function(t,e,o){},c821:function(t,e,o){"use strict";o("bfa7")},dd7b:function(t,e,o){"use strict";o.r(e);var n=function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("div",{staticClass:"login-container",staticStyle:{"background-image":"url('https://s1.ax1x.com/2020/11/09/BHG0gg.jpg')",width:"100vw",height:"100vh"}},[o("el-row",{staticStyle:{height:"100vh"}},[o("el-col",{staticClass:"parent",staticStyle:{height:"100%"},attrs:{xs:24,sm:24,md:24,lg:24,xl:24}},[o("el-form",{ref:"form",staticClass:"login-form",staticStyle:{width:"30vw"},attrs:{model:t.form,"label-width":"80px"}},[o("el-form-item",[o("h2",{staticStyle:{"text-align":"center","font-size":"55px",color:"white"}},[t._v("J N D C - Admin")])]),o("el-form-item",{attrs:{label:"用户名"}},[o("el-input",{attrs:{placeholder:"用户名"},model:{value:t.form.name,callback:function(e){t.$set(t.form,"name",e)},expression:"form.name"}})],1),o("el-form-item",{attrs:{label:"密码"}},[o("el-input",{ref:"password",attrs:{type:"password",placeholder:"密码"},nativeOn:{keyup:function(e){return!e.type.indexOf("key")&&t._k(e.keyCode,"enter",13,e.key,"Enter")?null:t.doLogin(e)}},model:{value:t.form.passWord,callback:function(e){t.$set(t.form,"passWord",e)},expression:"form.passWord"}})],1),o("el-form-item",{staticStyle:{"text-align":"right"}},[o("el-button",{attrs:{size:"mini",type:"primary"},on:{click:t.doLogin}},[t._v("登录")]),o("el-button",{attrs:{size:"mini"}},[t._v("取消")])],1)],1)],1)],1)],1)},a=[],s=o("b2cb"),i={name:"login",data:function(){return{form:{name:"",passWord:""}}},methods:{doLogin:function(){var t=this,e=this.$loading({lock:!0,text:"登陆中...",spinner:"el-icon-loading",background:"rgba(0, 0, 0, 0.7)"});Object(s["a"])({url:"/login",method:"post",data:this.form}).then((function(o){e.close(),"403"==o.token?t.$message.error("密码错误"):(localStorage.setItem("auth-token",o.token),t.$message.success("登录成功"),t.$router.push("management"))})).catch((function(){e.close()}))}}},r=i,l=(o("c821"),o("2877")),c=Object(l["a"])(r,n,a,!1,null,"61640559",null);e["default"]=c.exports}}]);
//# sourceMappingURL=chunk-45a0074c.db78a84e.js.map