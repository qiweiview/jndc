var W=Object.defineProperty;var A=Object.getOwnPropertySymbols;var _=Object.prototype.hasOwnProperty,q=Object.prototype.propertyIsEnumerable;var L=(t,e,a)=>e in t?W(t,e,{enumerable:!0,configurable:!0,writable:!0,value:a}):t[e]=a,w=(t,e)=>{for(var a in e||(e={}))_.call(e,a)&&L(t,a,e[a]);if(A)for(var a of A(e))q.call(e,a)&&L(t,a,e[a]);return t};var H=(t,e,a)=>new Promise((o,l)=>{var s=r=>{try{f(a.next(r))}catch(m){l(m)}},g=r=>{try{f(a.throw(r))}catch(m){l(m)}},f=r=>r.done?o(r.value):Promise.resolve(r.value).then(s,g);f((a=a.apply(t,e)).next())});import{aj as D,p as E,aJ as V,ac as G,l as z,N as T,a6 as C,m as v,T as Q,s as J,n as S,aK as K,a3 as x,aE as U,aL as X}from"./index-BjE2BxpS.js";import{u as k}from"./epTheme-Did4cZeb.js";function Y(){const{$storage:t,$config:e}=D(),a=()=>{var s,g,f,r,m,h,y,$,p,b,u,n,d,c,i;V().multiTagsCache&&(!t.tags||t.tags.length===0)&&(t.tags=G),t.layout||(t.layout={layout:(s=e==null?void 0:e.Layout)!=null?s:"vertical",theme:(g=e==null?void 0:e.Theme)!=null?g:"light",darkMode:(f=e==null?void 0:e.DarkMode)!=null?f:!1,sidebarStatus:(r=e==null?void 0:e.SidebarStatus)!=null?r:!0,epThemeColor:(m=e==null?void 0:e.EpThemeColor)!=null?m:"#409EFF",themeColor:(h=e==null?void 0:e.Theme)!=null?h:"light",overallStyle:(y=e==null?void 0:e.OverallStyle)!=null?y:"light"}),t.configure||(t.configure={grey:($=e==null?void 0:e.Grey)!=null?$:!1,weak:(p=e==null?void 0:e.Weak)!=null?p:!1,hideTabs:(b=e==null?void 0:e.HideTabs)!=null?b:!1,hideFooter:(u=e.HideFooter)!=null?u:!0,showLogo:(n=e==null?void 0:e.ShowLogo)!=null?n:!0,showModel:(d=e==null?void 0:e.ShowModel)!=null?d:"smart",multiTagsCache:(c=e==null?void 0:e.MultiTagsCache)!=null?c:!1,stretch:(i=e==null?void 0:e.Stretch)!=null?i:!1})},o=E(()=>t==null?void 0:t.layout.layout),l=E(()=>t.layout);return{layout:o,layoutTheme:l,initStorage:a}}const Z=z({id:"pure-app",state:()=>{var t,e,a,o;return{sidebar:{opened:(e=(t=T().getItem(`${C()}layout`))==null?void 0:t.sidebarStatus)!=null?e:v().SidebarStatus,withoutAnimation:!1,isClickCollapse:!1},layout:(o=(a=T().getItem(`${C()}layout`))==null?void 0:a.layout)!=null?o:v().Layout,device:Q()?"mobile":"desktop",viewportSize:{width:document.documentElement.clientWidth,height:document.documentElement.clientHeight}}},getters:{getSidebarStatus(t){return t.sidebar.opened},getDevice(t){return t.device},getViewportWidth(t){return t.viewportSize.width},getViewportHeight(t){return t.viewportSize.height}},actions:{TOGGLE_SIDEBAR(t,e){const a=T().getItem(`${C()}layout`);t&&e?(this.sidebar.withoutAnimation=!0,this.sidebar.opened=!0,a.sidebarStatus=!0):!t&&e?(this.sidebar.withoutAnimation=!0,this.sidebar.opened=!1,a.sidebarStatus=!1):!t&&!e&&(this.sidebar.withoutAnimation=!1,this.sidebar.opened=!this.sidebar.opened,this.sidebar.isClickCollapse=!this.sidebar.opened,a.sidebarStatus=this.sidebar.opened),T().setItem(`${C()}layout`,a)},toggleSideBar(t,e){return H(this,null,function*(){yield this.TOGGLE_SIDEBAR(t,e)})},toggleDevice(t){this.device=t},setLayout(t){this.layout=t},setViewportSize(t){this.viewportSize=t},setSortSwap(t){this.sortSwap=t}}});function ee(){return Z(J)}const B={outputDir:"",defaultScopeName:"",includeStyleWithColors:[],extract:!0,themeLinkTagId:"theme-link-tag",themeLinkTagInjectTo:"head",removeCssScopeName:!1,customThemeCssFileName:null,arbitraryMode:!1,defaultPrimaryColor:"",customThemeOutputPath:"/Users/liuqiwei/IdeaProjects/free-lite/free-lite-page/fll-web/node_modules/.pnpm/@pureadmin+theme@3.2.0/node_modules/@pureadmin/theme/setCustomTheme.js",styleTagId:"custom-theme-tagid",InjectDefaultStyleTagToHtml:!0,hueDiffControls:{low:0,high:0},multipleScopeVars:[{scopeName:"layout-theme-light",varsContent:`
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
      `}]},te="/",ae="assets",j=t=>{let e=t.replace("#","").match(/../g);for(let a=0;a<3;a++)e[a]=parseInt(e[a],16);return e},R=(t,e,a)=>{let o=[t.toString(16),e.toString(16),a.toString(16)];for(let l=0;l<3;l++)o[l].length==1&&(o[l]=`0${o[l]}`);return`#${o.join("")}`},oe=(t,e)=>{let a=j(t);for(let o=0;o<3;o++)a[o]=Math.floor(a[o]*(1-e));return R(a[0],a[1],a[2])},ue=(t,e)=>{let a=j(t);for(let o=0;o<3;o++)a[o]=Math.floor((255-a[o])*e+a[o]);return R(a[0],a[1],a[2])},N=t=>`(^${t}\\s+|\\s+${t}\\s+|\\s+${t}$|^${t}$)`,I=({scopeName:t,multipleScopeVars:e})=>{const a=Array.isArray(e)&&e.length?e:B.multipleScopeVars;let o=document.documentElement.className;new RegExp(N(t)).test(o)||(a.forEach(l=>{o=o.replace(new RegExp(N(l.scopeName),"g"),` ${t} `)}),document.documentElement.className=o.replace(/(^\s+|\s+$)/g,""))},P=({id:t,href:e})=>{const a=document.createElement("link");return a.rel="stylesheet",a.href=e,a.id=t,a},le=t=>{const e=w({scopeName:"theme-default",customLinkHref:s=>s},t),a=e.themeLinkTagId||B.themeLinkTagId;let o=document.getElementById(a);const l=e.customLinkHref(`${te.replace(/\/$/,"")}${`/${ae}/${e.scopeName}.css`.replace(/\/+(?=\/)/g,"")}`);if(o){o.id=`${a}_old`;const s=P({id:a,href:l});o.nextSibling?o.parentNode.insertBefore(s,o.nextSibling):o.parentNode.appendChild(s),s.onload=()=>{setTimeout(()=>{o.parentNode.removeChild(o),o=null},60),I(e)};return}o=P({id:a,href:l}),I(e),document[(e.themeLinkTagInjectTo||B.themeLinkTagInjectTo||"").replace("-prepend","")].appendChild(o)};function ie(){var p,b;const{layoutTheme:t,layout:e}=Y(),a=S([{color:"#ffffff",themeColor:"light"},{color:"#1b2a47",themeColor:"default"},{color:"#722ed1",themeColor:"saucePurple"},{color:"#eb2f96",themeColor:"pink"},{color:"#f5222d",themeColor:"dusk"},{color:"#fa541c",themeColor:"volcano"},{color:"#13c2c2",themeColor:"mingQing"},{color:"#52c41a",themeColor:"auroraGreen"}]),{$storage:o}=D(),l=S((p=o==null?void 0:o.layout)==null?void 0:p.darkMode),s=S((b=o==null?void 0:o.layout)==null?void 0:b.overallStyle),g=document.documentElement;function f(u,n,d){const c=d||document.body;let{className:i}=c;i=i.replace(n,"").trim(),c.className=u?`${i} ${n}`:i}function r(u=(d=>(d=v().Theme)!=null?d:"light")(),n=!0){var i,M;t.value.theme=u,le({scopeName:`layout-theme-${u}`});const c=o.layout.themeColor;if(o.layout={layout:e.value,theme:u,darkMode:l.value,sidebarStatus:(i=o.layout)==null?void 0:i.sidebarStatus,epThemeColor:(M=o.layout)==null?void 0:M.epThemeColor,themeColor:n?u:c,overallStyle:s.value},u==="default"||u==="light")h(v().EpThemeColor);else{const F=a.value.find(O=>O.themeColor===u);h(F.color)}}function m(u,n,d){document.documentElement.style.setProperty(`--el-color-primary-${u}-${n}`,l.value?oe(d,n/10):ue(d,n/10))}const h=u=>{k().setEpThemeColor(u),document.documentElement.style.setProperty("--el-color-primary",u);for(let n=1;n<=2;n++)m("dark",n,u);for(let n=1;n<=9;n++)m("light",n,u)};function y(u){s.value=u,k().epTheme==="light"&&l.value?r("default",!1):r(k().epTheme,!1),l.value?document.documentElement.classList.add("dark"):(o.layout.themeColor==="light"&&r("light",!1),document.documentElement.classList.remove("dark"))}function $(){K(),T().clear();const{Grey:u,Weak:n,MultiTagsCache:d,EpThemeColor:c,Layout:i}=v();ee().setLayout(i),h(c),x().multiTagsCacheChange(d),f(u,"html-grey",document.querySelector("html")),f(n,"html-weakness",document.querySelector("html")),U.push("/login"),x().handleTags("equal",[...G]),X()}return{body:g,dataTheme:l,overallStyle:s,layoutTheme:t,themeColors:a,onReset:$,toggleClass:f,dataThemeChange:y,setEpThemeColor:h,setLayoutThemeColor:r}}export{ee as a,Y as b,le as t,ie as u};
