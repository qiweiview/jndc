var W=Object.defineProperty;var A=Object.getOwnPropertySymbols;var _=Object.prototype.hasOwnProperty,q=Object.prototype.propertyIsEnumerable;var L=(t,e,a)=>e in t?W(t,e,{enumerable:!0,configurable:!0,writable:!0,value:a}):t[e]=a,w=(t,e)=>{for(var a in e||(e={}))_.call(e,a)&&L(t,a,e[a]);if(A)for(var a of A(e))q.call(e,a)&&L(t,a,e[a]);return t};var H=(t,e,a)=>new Promise((o,n)=>{var s=r=>{try{f(a.next(r))}catch(m){n(m)}},g=r=>{try{f(a.throw(r))}catch(m){n(m)}},f=r=>r.done?o(r.value):Promise.resolve(r.value).then(s,g);f((a=a.apply(t,e)).next())});import{ak as D,p as x,aQ as V,ad as j,l as z,N as T,a7 as C,m as v,U as Q,s as U,n as S,aR as J,a4 as E,aL as K,aS as X}from"./index-kZj4YhiH.js";import{u as k}from"./epTheme-MilMLfNq.js";function Y(){const{$storage:t,$config:e}=D(),a=()=>{var s,g,f,r,m,h,y,$,p,b,u,l,i,c,d;V().multiTagsCache&&(!t.tags||t.tags.length===0)&&(t.tags=j),t.layout||(t.layout={layout:(s=e==null?void 0:e.Layout)!=null?s:"vertical",theme:(g=e==null?void 0:e.Theme)!=null?g:"light",darkMode:(f=e==null?void 0:e.DarkMode)!=null?f:!1,sidebarStatus:(r=e==null?void 0:e.SidebarStatus)!=null?r:!0,epThemeColor:(m=e==null?void 0:e.EpThemeColor)!=null?m:"#409EFF",themeColor:(h=e==null?void 0:e.Theme)!=null?h:"light",overallStyle:(y=e==null?void 0:e.OverallStyle)!=null?y:"light"}),t.configure||(t.configure={grey:($=e==null?void 0:e.Grey)!=null?$:!1,weak:(p=e==null?void 0:e.Weak)!=null?p:!1,hideTabs:(b=e==null?void 0:e.HideTabs)!=null?b:!1,hideFooter:(u=e.HideFooter)!=null?u:!0,showLogo:(l=e==null?void 0:e.ShowLogo)!=null?l:!0,showModel:(i=e==null?void 0:e.ShowModel)!=null?i:"smart",multiTagsCache:(c=e==null?void 0:e.MultiTagsCache)!=null?c:!1,stretch:(d=e==null?void 0:e.Stretch)!=null?d:!1})},o=x(()=>t==null?void 0:t.layout.layout),n=x(()=>t.layout);return{layout:o,layoutTheme:n,initStorage:a}}const Z=z({id:"pure-app",state:()=>{var t,e,a,o;return{sidebar:{opened:(e=(t=T().getItem(`${C()}layout`))==null?void 0:t.sidebarStatus)!=null?e:v().SidebarStatus,withoutAnimation:!1,isClickCollapse:!1},layout:(o=(a=T().getItem(`${C()}layout`))==null?void 0:a.layout)!=null?o:v().Layout,device:Q()?"mobile":"desktop",viewportSize:{width:document.documentElement.clientWidth,height:document.documentElement.clientHeight}}},getters:{getSidebarStatus(t){return t.sidebar.opened},getDevice(t){return t.device},getViewportWidth(t){return t.viewportSize.width},getViewportHeight(t){return t.viewportSize.height}},actions:{TOGGLE_SIDEBAR(t,e){const a=T().getItem(`${C()}layout`);t&&e?(this.sidebar.withoutAnimation=!0,this.sidebar.opened=!0,a.sidebarStatus=!0):!t&&e?(this.sidebar.withoutAnimation=!0,this.sidebar.opened=!1,a.sidebarStatus=!1):!t&&!e&&(this.sidebar.withoutAnimation=!1,this.sidebar.opened=!this.sidebar.opened,this.sidebar.isClickCollapse=!this.sidebar.opened,a.sidebarStatus=this.sidebar.opened),T().setItem(`${C()}layout`,a)},toggleSideBar(t,e){return H(this,null,function*(){yield this.TOGGLE_SIDEBAR(t,e)})},toggleDevice(t){this.device=t},setLayout(t){this.layout=t},setViewportSize(t){this.viewportSize=t},setSortSwap(t){this.sortSwap=t}}});function ee(){return Z(U)}const B={outputDir:"",defaultScopeName:"",includeStyleWithColors:[],extract:!0,themeLinkTagId:"theme-link-tag",themeLinkTagInjectTo:"head",removeCssScopeName:!1,customThemeCssFileName:null,arbitraryMode:!1,defaultPrimaryColor:"",customThemeOutputPath:"/Users/liuqiwei/IdeaProjects/jndc/jndc-server-page/node_modules/.pnpm/@pureadmin+theme@3.2.0/node_modules/@pureadmin/theme/setCustomTheme.js",styleTagId:"custom-theme-tagid",InjectDefaultStyleTagToHtml:!0,hueDiffControls:{low:0,high:0},multipleScopeVars:[{scopeName:"layout-theme-light",varsContent:`
        $subMenuActiveText: #000000d9 !default;
        $menuBg: #fff !default;
        $menuHover: #f6f6f6 !default;
        $subMenuBg: #fff !default;
        $subMenuActiveBg: #e0ebf6 !default;
        $menuText: rgb(0 0 0 / 60%) !default;
        $sidebarLogo: #fff !default;
        $menuTitleHover: #000 !default;
        $menuActiveBefore: #4091f7 !default;
      `},{scopeName:"layout-theme-default",varsContent:`
        $subMenuActiveText: #fff !default;
        $menuBg: #001529 !default;
        $menuHover: rgb(64 145 247 / 15%) !default;
        $subMenuBg: #0f0303 !default;
        $subMenuActiveBg: #4091f7 !default;
        $menuText: rgb(254 254 254 / 65%) !default;
        $sidebarLogo: #002140 !default;
        $menuTitleHover: #fff !default;
        $menuActiveBefore: #4091f7 !default;
      `},{scopeName:"layout-theme-saucePurple",varsContent:`
        $subMenuActiveText: #fff !default;
        $menuBg: #130824 !default;
        $menuHover: rgb(105 58 201 / 15%) !default;
        $subMenuBg: #000 !default;
        $subMenuActiveBg: #693ac9 !default;
        $menuText: #7a80b4 !default;
        $sidebarLogo: #1f0c38 !default;
        $menuTitleHover: #fff !default;
        $menuActiveBefore: #693ac9 !default;
      `},{scopeName:"layout-theme-pink",varsContent:`
        $subMenuActiveText: #fff !default;
        $menuBg: #28081a !default;
        $menuHover: rgb(216 68 147 / 15%) !default;
        $subMenuBg: #000 !default;
        $subMenuActiveBg: #d84493 !default;
        $menuText: #7a80b4 !default;
        $sidebarLogo: #3f0d29 !default;
        $menuTitleHover: #fff !default;
        $menuActiveBefore: #d84493 !default;
      `},{scopeName:"layout-theme-dusk",varsContent:`
        $subMenuActiveText: #fff !default;
        $menuBg: #2a0608 !default;
        $menuHover: rgb(225 60 57 / 15%) !default;
        $subMenuBg: #000 !default;
        $subMenuActiveBg: #e13c39 !default;
        $menuText: rgb(254 254 254 / 65.1%) !default;
        $sidebarLogo: #42090c !default;
        $menuTitleHover: #fff !default;
        $menuActiveBefore: #e13c39 !default;
      `},{scopeName:"layout-theme-volcano",varsContent:`
        $subMenuActiveText: #fff !default;
        $menuBg: #2b0e05 !default;
        $menuHover: rgb(232 95 51 / 15%) !default;
        $subMenuBg: #0f0603 !default;
        $subMenuActiveBg: #e85f33 !default;
        $menuText: rgb(254 254 254 / 65%) !default;
        $sidebarLogo: #441708 !default;
        $menuTitleHover: #fff !default;
        $menuActiveBefore: #e85f33 !default;
      `},{scopeName:"layout-theme-mingQing",varsContent:`
        $subMenuActiveText: #fff !default;
        $menuBg: #032121 !default;
        $menuHover: rgb(89 191 193 / 15%) !default;
        $subMenuBg: #000 !default;
        $subMenuActiveBg: #59bfc1 !default;
        $menuText: #7a80b4 !default;
        $sidebarLogo: #053434 !default;
        $menuTitleHover: #fff !default;
        $menuActiveBefore: #59bfc1 !default;
      `},{scopeName:"layout-theme-auroraGreen",varsContent:`
        $subMenuActiveText: #fff !default;
        $menuBg: #0b1e15 !default;
        $menuHover: rgb(96 172 128 / 15%) !default;
        $subMenuBg: #000 !default;
        $subMenuActiveBg: #60ac80 !default;
        $menuText: #7a80b4 !default;
        $sidebarLogo: #112f21 !default;
        $menuTitleHover: #fff !default;
        $menuActiveBefore: #60ac80 !default;
      `}]},te="/",ae="assets",G=t=>{let e=t.replace("#","").match(/../g);for(let a=0;a<3;a++)e[a]=parseInt(e[a],16);return e},R=(t,e,a)=>{let o=[t.toString(16),e.toString(16),a.toString(16)];for(let n=0;n<3;n++)o[n].length==1&&(o[n]=`0${o[n]}`);return`#${o.join("")}`},oe=(t,e)=>{let a=G(t);for(let o=0;o<3;o++)a[o]=Math.floor(a[o]*(1-e));return R(a[0],a[1],a[2])},ue=(t,e)=>{let a=G(t);for(let o=0;o<3;o++)a[o]=Math.floor((255-a[o])*e+a[o]);return R(a[0],a[1],a[2])},N=t=>`(^${t}\\s+|\\s+${t}\\s+|\\s+${t}$|^${t}$)`,I=({scopeName:t,multipleScopeVars:e})=>{const a=Array.isArray(e)&&e.length?e:B.multipleScopeVars;let o=document.documentElement.className;new RegExp(N(t)).test(o)||(a.forEach(n=>{o=o.replace(new RegExp(N(n.scopeName),"g"),` ${t} `)}),document.documentElement.className=o.replace(/(^\s+|\s+$)/g,""))},P=({id:t,href:e})=>{const a=document.createElement("link");return a.rel="stylesheet",a.href=e,a.id=t,a},ne=t=>{const e=w({scopeName:"theme-default",customLinkHref:s=>s},t),a=e.themeLinkTagId||B.themeLinkTagId;let o=document.getElementById(a);const n=e.customLinkHref(`${te.replace(/\/$/,"")}${`/${ae}/${e.scopeName}.css`.replace(/\/+(?=\/)/g,"")}`);if(o){o.id=`${a}_old`;const s=P({id:a,href:n});o.nextSibling?o.parentNode.insertBefore(s,o.nextSibling):o.parentNode.appendChild(s),s.onload=()=>{setTimeout(()=>{o.parentNode.removeChild(o),o=null},60),I(e)};return}o=P({id:a,href:n}),I(e),document[(e.themeLinkTagInjectTo||B.themeLinkTagInjectTo||"").replace("-prepend","")].appendChild(o)};function de(){var p,b;const{layoutTheme:t,layout:e}=Y(),a=S([{color:"#ffffff",themeColor:"light"},{color:"#1b2a47",themeColor:"default"},{color:"#722ed1",themeColor:"saucePurple"},{color:"#eb2f96",themeColor:"pink"},{color:"#f5222d",themeColor:"dusk"},{color:"#fa541c",themeColor:"volcano"},{color:"#13c2c2",themeColor:"mingQing"},{color:"#52c41a",themeColor:"auroraGreen"}]),{$storage:o}=D(),n=S((p=o==null?void 0:o.layout)==null?void 0:p.darkMode),s=S((b=o==null?void 0:o.layout)==null?void 0:b.overallStyle),g=document.documentElement;function f(u,l,i){const c=i||document.body;let{className:d}=c;d=d.replace(l,"").trim(),c.className=u?`${d} ${l}`:d}function r(u=(i=>(i=v().Theme)!=null?i:"light")(),l=!0){var d,M;t.value.theme=u,ne({scopeName:`layout-theme-${u}`});const c=o.layout.themeColor;if(o.layout={layout:e.value,theme:u,darkMode:n.value,sidebarStatus:(d=o.layout)==null?void 0:d.sidebarStatus,epThemeColor:(M=o.layout)==null?void 0:M.epThemeColor,themeColor:l?u:c,overallStyle:s.value},u==="default"||u==="light")h(v().EpThemeColor);else{const F=a.value.find(O=>O.themeColor===u);h(F.color)}}function m(u,l,i){document.documentElement.style.setProperty(`--el-color-primary-${u}-${l}`,n.value?oe(i,l/10):ue(i,l/10))}const h=u=>{k().setEpThemeColor(u),document.documentElement.style.setProperty("--el-color-primary",u);for(let l=1;l<=2;l++)m("dark",l,u);for(let l=1;l<=9;l++)m("light",l,u)};function y(u){s.value=u,k().epTheme==="light"&&n.value?r("default",!1):r(k().epTheme,!1),n.value?document.documentElement.classList.add("dark"):(o.layout.themeColor==="light"&&r("light",!1),document.documentElement.classList.remove("dark"))}function $(){J(),T().clear();const{Grey:u,Weak:l,MultiTagsCache:i,EpThemeColor:c,Layout:d}=v();ee().setLayout(d),h(c),E().multiTagsCacheChange(i),f(u,"html-grey",document.querySelector("html")),f(l,"html-weakness",document.querySelector("html")),K.push("/login"),E().handleTags("equal",[...j]),X()}return{body:g,dataTheme:n,overallStyle:s,layoutTheme:t,themeColors:a,onReset:$,toggleClass:f,dataThemeChange:y,setEpThemeColor:h,setLayoutThemeColor:r}}export{ee as a,Y as b,ne as t,de as u};
