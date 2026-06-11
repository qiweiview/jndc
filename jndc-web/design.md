请按以下 UI/UX 规范设计 Web 系统：

整体风格：蓝白色、清爽、克制、偏飞书/Lark 工作台风格。界面要轻、留白充足、信息层级清晰，避免厚重阴影、花哨渐变和高饱和装饰。

技术基础：已引入 Ant Design，优先使用 antd 组件，不要重复造轮子。通过 ConfigProvider theme token 统一风格。Ant Design 支持通过 token 自定义主色、圆角、边框等主题变量。参考 antd 的 8px 栅格体系和 24 栅格布局。:contentReference[oaicite:0]{index=0}

主题色：
- Primary：#3370FF
- Primary Hover：#4E83FD
- Primary Active：#245BDB
- 背景：#F7F9FC / #F5F7FA
- 卡片/面板：#FFFFFF
- 主文字：#1F2329
- 次文字：#646A73
- 弱文字：#8F959E
- 边框：#DEE0E3
- 分割线：#EFF0F1
- 成功：#00B578
- 警告：#FFB020
- 错误：#F54A45

布局规范：
- 页面背景使用浅灰蓝，不使用纯灰暗背景。
- 主内容区使用白色 Card/Panel 承载。
- 页面左右 padding：24px；复杂后台页面可用 32px。
- 模块间距：24px；卡片内部 padding：20px 或 24px。
- 所有间距遵循 8px 倍数。
- 顶部导航高度 56px~64px。
- 左侧菜单宽度 220px~240px。
- 内容最大宽度根据场景控制，表单页建议 960px 内。

视觉规范：
- 圆角：8px，弹窗/大卡片可 12px。
- 阴影要轻：仅用于浮层、Dropdown、Modal、Popover。
- 边框优先于阴影。
- 图标使用线性图标，尺寸 16/18/20px。
- 不使用大面积渐变、不使用玻璃拟态、不使用霓虹色。

字体：
- 默认使用系统字体：-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial。
- 页面标题：20px / 28px，font-weight 600。
- 区块标题：16px / 24px，font-weight 600。
- 正文：14px / 22px。
- 辅助说明：12px / 20px。
- 表格内容保持 14px，避免过密。

组件使用：
- Button：主按钮只用于核心动作；同一区域最多一个 primary。
- Table：表头浅灰背景，行高舒适，操作列靠右，危险操作用文字按钮并二次确认。
- Form：标签左对齐或顶部对齐，错误提示紧贴字段下方。
- Modal：标题明确，底部按钮右对齐，主操作在最右。
- Drawer：用于详情、编辑、辅助流程，避免频繁跳页。
- Card：用于信息分组，不要过度嵌套。
- Tag：低饱和浅底色，不要使用过多颜色。
- Empty/Loading/Error：必须有友好状态，不留空白页。

交互规范：
- 所有可点击元素 hover 有轻微反馈。
- 保存、删除、提交必须有 loading 状态。
- 危险操作必须 Confirm。
- 表单提交成功用 message.success，失败用 message.error。
- 长流程优先使用 Drawer 或步骤条。
- 数据为空时显示 Empty，并提供下一步操作。

页面气质：
- 像飞书一样“轻办公、可信赖、高效率”。
- 信息密度中等，不要像传统后台那样拥挤。
- 优先清晰、安静、专业，而不是炫酷。
- 蓝色只用于引导和关键状态，不要满屏蓝。

Ant Design 主题示例：

const theme = {
token: {
colorPrimary: '#3370FF',
colorInfo: '#3370FF',
colorSuccess: '#00B578',
colorWarning: '#FFB020',
colorError: '#F54A45',
colorText: '#1F2329',
colorTextSecondary: '#646A73',
colorTextTertiary: '#8F959E',
colorBorder: '#DEE0E3',
colorSplit: '#EFF0F1',
colorBgLayout: '#F7F9FC',
colorBgContainer: '#FFFFFF',
borderRadius: 8,
fontSize: 14,
wireframe: false
},
components: {
Button: {
borderRadius: 8,
controlHeight: 36
},
Input: {
borderRadius: 8,
controlHeight: 36
},
Select: {
borderRadius: 8,
controlHeight: 36
},
Card: {
borderRadiusLG: 12,
paddingLG: 24
},
Table: {
headerBg: '#F7F9FC',
headerColor: '#1F2329',
rowHoverBg: '#F5F8FF'
},
Menu: {
itemSelectedBg: '#EAF1FF',
itemSelectedColor: '#3370FF'
}
}
};

输出要求：
- 生成页面时必须遵守以上规范。
- 优先复用 antd Layout、Menu、Card、Table、Form、Button、Drawer、Modal、Tag、Empty、Spin、Message。
- 不要引入新的 UI 库。
- CSS 要模块化，避免全局污染。
- 设计结果应看起来像现代 SaaS/协作办公系统。