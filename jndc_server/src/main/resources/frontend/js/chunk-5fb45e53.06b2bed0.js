(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-5fb45e53"],{"0821":function(e,t,n){"use strict";n.r(t);var r=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-row",{staticStyle:{padding:"10px"}},[n("el-col",{attrs:{xs:24,sm:24,md:24,lg:24,xl:24}},[n("el-tabs",{on:{"tab-click":e.clickTab},model:{value:e.activeName,callback:function(t){e.activeName=t},expression:"activeName"}},[n("el-tab-pane",{attrs:{label:"隧道列表",name:"a"}},[n("el-input",{staticStyle:{width:"20%"},attrs:{clearable:"",placeholder:"筛选隧道编号或IP"},on:{change:e.getServerChannelTable},model:{value:e.searchKey,callback:function(t){e.searchKey=t},expression:"searchKey"}}),n("el-button",{staticStyle:{"margin-left":"15px"},attrs:{size:"mini"},on:{click:e.getServerChannelTable}},[e._v("查询")]),n("el-table",{attrs:{data:e.displayArray}},[n("el-table-column",{attrs:{label:"隧道编号"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",[e._v(e._s(t.row.id))])]}}])}),n("el-table-column",{attrs:{label:"隧道来源"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",[e._v(e._s(t.row.channelClientIp)+":"+e._s(t.row.channelClientPort))])]}}])}),n("el-table-column",{attrs:{width:"100px",label:"服务数"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",{staticStyle:{cursor:"pointer",color:"deepskyblue","text-align":"left"},on:{click:function(n){return e.toServicePage(t.row.id)}}},[e._v(e._s(t.row.supportServiceNum))])]}}])}),n("el-table-column",{attrs:{width:"150px",label:"最后心跳时间"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",{},[e._v(e._s(new Date(t.row.lastHearBeatTimeStamp).Format("yyyy-MM-dd HH:mm:ss")))])]}}])}),n("el-table-column",{attrs:{label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("el-button",{attrs:{size:"mini",type:"danger"},on:{click:function(n){return e.closeChannelByServer(t.row.id,t.row.supportServiceNum)}}},[e._v("断 开 ")]),n("el-button",{attrs:{size:"mini",type:"success"},on:{click:function(n){return e.sendHeartBeat(t.row.id)}}},[e._v("发 送 心 跳 ")])]}}])})],1)],1),n("el-tab-pane",{attrs:{label:"中断记录",name:"b"}},[n("el-button",{attrs:{size:"mini",type:"info"},on:{click:e.getChannelRecord}},[e._v("刷 新")]),n("el-button",{attrs:{size:"mini",type:"danger"},on:{click:e.clearChannelRecord}},[e._v("清 空")]),n("el-table",{attrs:{data:e.channelRecordArray,"max-height":"85vh"}},[n("el-table-column",{attrs:{label:"隧道编号"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",{staticStyle:{"text-align":"left"}},[e._v(e._s(t.row.channelId))])]}}])}),n("el-table-column",{attrs:{label:"隧道客户端IP"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",{},[e._v(e._s(t.row.ip))])]}}])}),n("el-table-column",{attrs:{label:"隧道客户端端口"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",{},[e._v(e._s(t.row.port))])]}}])}),n("el-table-column",{attrs:{label:"连接时间"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",{staticStyle:{"text-align":"center"}},[e._v(e._s(new Date(t.row.timeStamp).Format("yyyy-MM-dd HH:mm:ss")))])]}}])})],1),n("el-pagination",{attrs:{background:"","page-sizes":[10,15,30,100],"page-size":e.recordRows,layout:"sizes, prev, pager, next",total:e.chanelRecordTotal},on:{"size-change":e.sizeChange,"current-change":e.pageChange}})],1)],1)],1)],1)},a=[],o=(n("4160"),n("c975"),n("b0c0"),n("4d63"),n("ac1f"),n("25f0"),n("5319"),n("159b"),n("b2cb")),c=n("0aef");Date.prototype.Format=function(e){var t={"M+":this.getMonth()+1,"d+":this.getDate(),"H+":this.getHours(),"m+":this.getMinutes(),"s+":this.getSeconds(),"q+":Math.floor((this.getMonth()+3)/3),S:this.getMilliseconds()};for(var n in/(y+)/.test(e)&&(e=e.replace(RegExp.$1,(this.getFullYear()+"").substr(4-RegExp.$1.length))),t)new RegExp("("+n+")").test(e)&&(e=e.replace(RegExp.$1,1==RegExp.$1.length?t[n]:("00"+t[n]).substr((""+t[n]).length)));return e};var i={name:"channelList",data:function(){return{searchKey:"",storeArray:[],displayArray:[],channelRecordArray:[],activeName:"a",recordCurrentPage:1,chanelRecordTotal:0,recordRows:10}},methods:{toServicePage:function(e){this.$router.push({path:"/management/services",query:{clientId:e}})},clearChannelRecord:function(){var e=this;this.$confirm("清空连接纪录后将不可恢复?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then((function(){Object(o["a"])({url:"/clearChannelRecord",method:"post",data:{}}).then((function(t){200==t.code?(e.$message.success(t.message),e.getChannelRecord()):e.$message.error(t.message)})).catch((function(){}))})).catch((function(){}))},clickTab:function(e){"b"==e.name&&this.getChannelRecord()},sizeChange:function(e){this.recordRows=e,this.getChannelRecord()},pageChange:function(e){this.recordCurrentPage=e,this.getChannelRecord()},getChannelRecord:function(){var e=this;Object(o["a"])({url:"/getChannelRecord",method:"post",data:{page:this.recordCurrentPage,rows:this.recordRows}}).then((function(t){e.channelRecordArray=t.data,e.chanelRecordTotal=t.total})).catch((function(){}))},sendHeartBeat:function(e){var t=this;Object(o["a"])({url:"/sendHeartBeat",method:"post",data:{id:e}}).then((function(e){200==e.code?t.$message.success(e.message):t.$message.error(e.message)})).catch((function(){}))},closeChannelByServer:function(e,t){var n=this;this.$confirm("断开隧道后,隧道提供的："+t+"项服务将不再被使用，是否继续?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then((function(){Object(o["a"])({url:"/closeChannelByServer",method:"post",data:{id:e}}).then((function(e){200==e.code?(n.$message.success(e.message),n.getServerChannelTable()):n.$message.error(e.message)})).catch((function(){}))})).catch((function(){}))},getServerChannelTable:function(){var e=this,t=this.$loading({lock:!0,text:"Loading",spinner:"el-icon-loading",background:"rgba(0, 0, 0, 0.7)"});Object(o["a"])({url:"/getServerChannelTable",method:"post",data:{}}).then((function(n){e.storeArray=n,e.conditionalRendering(),t.close()})).catch((function(){t.close()}))},conditionalRendering:function(){var e=this;if(""==this.searchKey)this.displayArray=this.storeArray;else{var t=[];this.storeArray.forEach((function(n){-1===n.id.indexOf(e.searchKey)&&-1===n.channelClientIp.indexOf(e.searchKey)||t.push(n)})),this.displayArray=t}}},mounted:function(){this.getServerChannelTable(),c["a"].registerPage("channelList","隧道列表",this.getServerChannelTable)}},s=i,l=n("2877"),u=Object(l["a"])(s,r,a,!1,null,"6d16c06f",null);t["default"]=u.exports},"0aef":function(e,t,n){"use strict";n("4160");var r=n("5c96"),a={},o={},c=[];a.registerPage=function(e,t,n){c.push({pageName:e,pageDescription:t,callBack:n})},a.parseMessage=function(e){var t=JSON.parse(e);1==t.type?c.forEach((function(e){e.pageName==t.data&&(e.callBack(),console.log(e.pageDescription+"数据刷新"))})):(console.log("数据",e),r["Notification"].info({title:"通知",message:t.data,position:"bottom-right"}))},a.find=function(e){return o[e]},a.create=function(e,t){"undefined"!=typeof o[e]&&console.error("the object:"+e+" exist,the older socket has been covered");var n={name:e,onmessage:function(e){console.log("default action: receive "+e)},onopen:function(){console.log("default action: open ws ")},onclose:function(){console.log("default action: close ws ")}},r="ws://"+window.location.host+"/"+t+"?auth-token="+localStorage.getItem("auth-token");console.log(r,r);var a=new WebSocket(r);return a.onopen=function(){try{n.onopen()}catch(e){console.error("can not found the method onopen() by singleWebSocketHolder")}},a.onmessage=function(e){var t=e.data;try{n.onmessage(t)}catch(r){console.error(r)}},a.onclose=function(){try{n.onclose()}catch(e){console.error("can not found the method onclose() by singleWebSocketHolder")}},n["object"]=a,o[e]=n,n},t["a"]=a},"14c3":function(e,t,n){var r=n("c6b6"),a=n("9263");e.exports=function(e,t){var n=e.exec;if("function"===typeof n){var o=n.call(e,t);if("object"!==typeof o)throw TypeError("RegExp exec method returned something other than an Object or null");return o}if("RegExp"!==r(e))throw TypeError("RegExp#exec called on incompatible receiver");return a.call(e,t)}},"159b":function(e,t,n){var r=n("da84"),a=n("fdbc"),o=n("17c2"),c=n("9112");for(var i in a){var s=r[i],l=s&&s.prototype;if(l&&l.forEach!==o)try{c(l,"forEach",o)}catch(u){l.forEach=o}}},"25f0":function(e,t,n){"use strict";var r=n("6eeb"),a=n("825a"),o=n("d039"),c=n("ad6d"),i="toString",s=RegExp.prototype,l=s[i],u=o((function(){return"/a/b"!=l.call({source:"a",flags:"b"})})),f=l.name!=i;(u||f)&&r(RegExp.prototype,i,(function(){var e=a(this),t=String(e.source),n=e.flags,r=String(void 0===n&&e instanceof RegExp&&!("flags"in s)?c.call(e):n);return"/"+t+"/"+r}),{unsafe:!0})},"44e7":function(e,t,n){var r=n("861d"),a=n("c6b6"),o=n("b622"),c=o("match");e.exports=function(e){var t;return r(e)&&(void 0!==(t=e[c])?!!t:"RegExp"==a(e))}},"4d63":function(e,t,n){var r=n("83ab"),a=n("da84"),o=n("94ca"),c=n("7156"),i=n("9bf2").f,s=n("241c").f,l=n("44e7"),u=n("ad6d"),f=n("9f7f"),d=n("6eeb"),h=n("d039"),p=n("69f3").set,g=n("2626"),v=n("b622"),b=v("match"),y=a.RegExp,m=y.prototype,x=/a/g,S=/a/g,E=new y(x)!==x,R=f.UNSUPPORTED_Y,_=r&&o("RegExp",!E||R||h((function(){return S[b]=!1,y(x)!=x||y(S)==S||"/a/i"!=y(x,"i")})));if(_){var w=function(e,t){var n,r=this instanceof w,a=l(e),o=void 0===t;if(!r&&a&&e.constructor===w&&o)return e;E?a&&!o&&(e=e.source):e instanceof w&&(o&&(t=u.call(e)),e=e.source),R&&(n=!!t&&t.indexOf("y")>-1,n&&(t=t.replace(/y/g,"")));var i=c(E?new y(e,t):y(e,t),r?this:m,w);return R&&n&&p(i,{sticky:n}),i},C=function(e){e in w||i(w,e,{configurable:!0,get:function(){return y[e]},set:function(t){y[e]=t}})},T=s(y),k=0;while(T.length>k)C(T[k++]);m.constructor=w,w.prototype=m,d(a,"RegExp",w)}g("RegExp")},5319:function(e,t,n){"use strict";var r=n("d784"),a=n("825a"),o=n("7b0b"),c=n("50c4"),i=n("a691"),s=n("1d80"),l=n("8aa5"),u=n("14c3"),f=Math.max,d=Math.min,h=Math.floor,p=/\$([$&'`]|\d\d?|<[^>]*>)/g,g=/\$([$&'`]|\d\d?)/g,v=function(e){return void 0===e?e:String(e)};r("replace",2,(function(e,t,n,r){var b=r.REGEXP_REPLACE_SUBSTITUTES_UNDEFINED_CAPTURE,y=r.REPLACE_KEEPS_$0,m=b?"$":"$0";return[function(n,r){var a=s(this),o=void 0==n?void 0:n[e];return void 0!==o?o.call(n,a,r):t.call(String(a),n,r)},function(e,r){if(!b&&y||"string"===typeof r&&-1===r.indexOf(m)){var o=n(t,e,this,r);if(o.done)return o.value}var s=a(e),h=String(this),p="function"===typeof r;p||(r=String(r));var g=s.global;if(g){var S=s.unicode;s.lastIndex=0}var E=[];while(1){var R=u(s,h);if(null===R)break;if(E.push(R),!g)break;var _=String(R[0]);""===_&&(s.lastIndex=l(h,c(s.lastIndex),S))}for(var w="",C=0,T=0;T<E.length;T++){R=E[T];for(var k=String(R[0]),L=f(d(i(R.index),h.length),0),A=[],P=1;P<R.length;P++)A.push(v(R[P]));var $=R.groups;if(p){var I=[k].concat(A,L,h);void 0!==$&&I.push($);var M=String(r.apply(void 0,I))}else M=x(k,h,L,A,$,r);L>=C&&(w+=h.slice(C,L)+M,C=L+k.length)}return w+h.slice(C)}];function x(e,n,r,a,c,i){var s=r+e.length,l=a.length,u=g;return void 0!==c&&(c=o(c),u=p),t.call(i,u,(function(t,o){var i;switch(o.charAt(0)){case"$":return"$";case"&":return e;case"`":return n.slice(0,r);case"'":return n.slice(s);case"<":i=c[o.slice(1,-1)];break;default:var u=+o;if(0===u)return t;if(u>l){var f=h(u/10);return 0===f?t:f<=l?void 0===a[f-1]?o.charAt(1):a[f-1]+o.charAt(1):t}i=a[u-1]}return void 0===i?"":i}))}}))},6547:function(e,t,n){var r=n("a691"),a=n("1d80"),o=function(e){return function(t,n){var o,c,i=String(a(t)),s=r(n),l=i.length;return s<0||s>=l?e?"":void 0:(o=i.charCodeAt(s),o<55296||o>56319||s+1===l||(c=i.charCodeAt(s+1))<56320||c>57343?e?i.charAt(s):o:e?i.slice(s,s+2):c-56320+(o-55296<<10)+65536)}};e.exports={codeAt:o(!1),charAt:o(!0)}},7156:function(e,t,n){var r=n("861d"),a=n("d2bb");e.exports=function(e,t,n){var o,c;return a&&"function"==typeof(o=t.constructor)&&o!==n&&r(c=o.prototype)&&c!==n.prototype&&a(e,c),e}},"8aa5":function(e,t,n){"use strict";var r=n("6547").charAt;e.exports=function(e,t,n){return t+(n?r(e,t).length:1)}},9263:function(e,t,n){"use strict";var r=n("ad6d"),a=n("9f7f"),o=RegExp.prototype.exec,c=String.prototype.replace,i=o,s=function(){var e=/a/,t=/b*/g;return o.call(e,"a"),o.call(t,"a"),0!==e.lastIndex||0!==t.lastIndex}(),l=a.UNSUPPORTED_Y||a.BROKEN_CARET,u=void 0!==/()??/.exec("")[1],f=s||u||l;f&&(i=function(e){var t,n,a,i,f=this,d=l&&f.sticky,h=r.call(f),p=f.source,g=0,v=e;return d&&(h=h.replace("y",""),-1===h.indexOf("g")&&(h+="g"),v=String(e).slice(f.lastIndex),f.lastIndex>0&&(!f.multiline||f.multiline&&"\n"!==e[f.lastIndex-1])&&(p="(?: "+p+")",v=" "+v,g++),n=new RegExp("^(?:"+p+")",h)),u&&(n=new RegExp("^"+p+"$(?!\\s)",h)),s&&(t=f.lastIndex),a=o.call(d?n:f,v),d?a?(a.input=a.input.slice(g),a[0]=a[0].slice(g),a.index=f.lastIndex,f.lastIndex+=a[0].length):f.lastIndex=0:s&&a&&(f.lastIndex=f.global?a.index+a[0].length:t),u&&a&&a.length>1&&c.call(a[0],n,(function(){for(i=1;i<arguments.length-2;i++)void 0===arguments[i]&&(a[i]=void 0)})),a}),e.exports=i},"9f7f":function(e,t,n){"use strict";var r=n("d039");function a(e,t){return RegExp(e,t)}t.UNSUPPORTED_Y=r((function(){var e=a("a","y");return e.lastIndex=2,null!=e.exec("abcd")})),t.BROKEN_CARET=r((function(){var e=a("^r","gy");return e.lastIndex=2,null!=e.exec("str")}))},ac1f:function(e,t,n){"use strict";var r=n("23e7"),a=n("9263");r({target:"RegExp",proto:!0,forced:/./.exec!==a},{exec:a})},ad6d:function(e,t,n){"use strict";var r=n("825a");e.exports=function(){var e=r(this),t="";return e.global&&(t+="g"),e.ignoreCase&&(t+="i"),e.multiline&&(t+="m"),e.dotAll&&(t+="s"),e.unicode&&(t+="u"),e.sticky&&(t+="y"),t}},b0c0:function(e,t,n){var r=n("83ab"),a=n("9bf2").f,o=Function.prototype,c=o.toString,i=/^\s*function ([^ (]*)/,s="name";r&&!(s in o)&&a(o,s,{configurable:!0,get:function(){try{return c.call(this).match(i)[1]}catch(e){return""}}})},d784:function(e,t,n){"use strict";n("ac1f");var r=n("6eeb"),a=n("d039"),o=n("b622"),c=n("9263"),i=n("9112"),s=o("species"),l=!a((function(){var e=/./;return e.exec=function(){var e=[];return e.groups={a:"7"},e},"7"!=="".replace(e,"$<a>")})),u=function(){return"$0"==="a".replace(/./,"$0")}(),f=o("replace"),d=function(){return!!/./[f]&&""===/./[f]("a","$0")}(),h=!a((function(){var e=/(?:)/,t=e.exec;e.exec=function(){return t.apply(this,arguments)};var n="ab".split(e);return 2!==n.length||"a"!==n[0]||"b"!==n[1]}));e.exports=function(e,t,n,f){var p=o(e),g=!a((function(){var t={};return t[p]=function(){return 7},7!=""[e](t)})),v=g&&!a((function(){var t=!1,n=/a/;return"split"===e&&(n={},n.constructor={},n.constructor[s]=function(){return n},n.flags="",n[p]=/./[p]),n.exec=function(){return t=!0,null},n[p](""),!t}));if(!g||!v||"replace"===e&&(!l||!u||d)||"split"===e&&!h){var b=/./[p],y=n(p,""[e],(function(e,t,n,r,a){return t.exec===c?g&&!a?{done:!0,value:b.call(t,n,r)}:{done:!0,value:e.call(n,t,r)}:{done:!1}}),{REPLACE_KEEPS_$0:u,REGEXP_REPLACE_SUBSTITUTES_UNDEFINED_CAPTURE:d}),m=y[0],x=y[1];r(String.prototype,e,m),r(RegExp.prototype,p,2==t?function(e,t){return x.call(e,this,t)}:function(e){return x.call(e,this)})}f&&i(RegExp.prototype[p],"sham",!0)}},fdbc:function(e,t){e.exports={CSSRuleList:0,CSSStyleDeclaration:0,CSSValueList:0,ClientRectList:0,DOMRectList:0,DOMStringList:0,DOMTokenList:1,DataTransferItemList:0,FileList:0,HTMLAllCollection:0,HTMLCollection:0,HTMLFormElement:0,HTMLSelectElement:0,MediaList:0,MimeTypeArray:0,NamedNodeMap:0,NodeList:1,PaintRequestList:0,Plugin:0,PluginArray:0,SVGLengthList:0,SVGNumberList:0,SVGPathSegList:0,SVGPointList:0,SVGStringList:0,SVGTransformList:0,SourceBufferList:0,StyleSheetList:0,TextTrackCueList:0,TextTrackList:0,TouchList:0}}}]);
//# sourceMappingURL=chunk-5fb45e53.06b2bed0.js.map