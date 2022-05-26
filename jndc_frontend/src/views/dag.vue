<template>
    <el-row>
        <el-col :xs="3" :sm="3" :md="3" :lg="3" :xl="3">
            <el-collapse accordion>
                <el-collapse-item v-for="group in toolbarGroup" :key="group.name" :title="group.name"
                                  :name="group.name">
                    <div v-for="item in group.graphArray" :key="item['title']" ref="toolItem" :data-width="item.width"
                         :data-height="item.height">
                        <img :src="item.icon" :alt="item.title" style="width: 64px;height: 64px">
                        <span style="margin-left: 15px">{{item['title']}}</span>
                    </div>
                </el-collapse-item>
            </el-collapse>

            <!--            <div class="customToolbarContainer">-->
            <!--                <div class="toolbarContainer">-->
            <!--                    <div v-for="item in toolbarItems" :key="item['title']" ref="toolItem">-->
            <!--                        <img :src="item['icon']" :alt="item['title']" style="width: 64px;height: 64px">-->
            <!--                        <span style="margin-left: 15px">{{item['title']}}</span>-->
            <!--                    </div>-->
            <!--                </div>-->
            <!--            </div>-->
        </el-col>
        <el-col :xs="17" :sm="17" :md="17" :lg="17" :xl="17">
            <div id="g1"
                 style="position:relative;overflow:hidden;width:100%;height:90vh;background:url('grid.gif');cursor:default;"></div>
        </el-col>
        <el-col :xs="4" :sm="4" :md="4" :lg="4" :xl="4">
            <div v-show="currentOptionPage=='un_choose'">
                <el-button type="primary" size="mini" @click="calculate">计算</el-button>

            </div>

            <action_edg ref="action_edg" v-show="currentOptionPage=='action_edg'"/>

            <db_vertex ref="db_vertex" @submit="dbSubmit" v-show="currentOptionPage=='db_vertex'"/>

            <agg_vertex ref="agg_vertex" @submit="dbSubmit" v-show="currentOptionPage=='agg_vertex'"/>
            <average_vertex ref="average_vertex" @submit="dbSubmit" v-show="currentOptionPage=='average_vertex'"/>
            <max_vertex ref="max_vertex" @submit="dbSubmit" v-show="currentOptionPage=='max_vertex'"/>
            <merge_vertex ref="merge_vertex" @submit="dbSubmit" v-show="currentOptionPage=='merge_vertex'"/>
            <min_vertex ref="min_vertex" @submit="dbSubmit" v-show="currentOptionPage=='min_vertex'"/>
            <sum_vertex ref="sum_vertex" @submit="dbSubmit" v-show="currentOptionPage=='sum_vertex'"/>
        </el-col>
    </el-row>
</template>

<script>
    import request from "@/config/requestConfig";

    import mxgraph from "@/config/mxgraph";

    import toolbarGroup from "@/config/toolbar";


    const {
        mxGraph, mxClient, mxUtils,
        mxShape, mxConnectionConstraint, mxPoint,
        mxPolyline, mxRubberband, mxEvent,
        mxKeyHandler, mxUndoManager
    } = mxgraph;

    export default {
        name: "login",
        computed: {
            toolbarGroup: () => toolbarGroup
        },
        data() {
            return {
                choose_id: '',
                toolbarGroupAll: [],
                dataPool: {},
                currentOptionPage: "un_choose",
                parent: {},
                g1: {},
                graph: {},
                toolbar: {},
                undoManager: {}
            }
        },
        methods: {
            cancel() {
                this.graph.clearSelection()
                this.currentOptionPage = "un_choose"
            },
            sendDAG(line, point) {
                let that = this

                let merge = []

                point.forEach(x => {

                    if ('merge_vertex' == x.component) {
                        let data = that.dataPool[x.unique_id]
                        data.id = x.unique_id
                        merge.push(data)
                    }

                })

                let body = {
                    edgDTOList: [],
                    vertexDTOList: []
                }


                let vertexMap = {}

                merge.forEach(x => {
                    x.nearSources.forEach(y => {
                        let edg = {source: y.id, target: x.id, operationDTOS: []}
                        vertexMap[y.id] = {id: y.id, vertexType: 'source'}
                        vertexMap[x.id] = {id: x.id, vertexType: 'target'}
                        y.mergeColumns.forEach(z => {
                            edg.operationDTOS.push({operation: z.detail, index: z.index, type: z.type})
                        })
                        body.edgDTOList.push(edg)
                    })
                })

                for (let key in vertexMap) {
                    let dataSet = {rows: []}
                    let sg = vertexMap[key]
                    let data = this.dataPool[key]
                    let vertex = {id: key, vertexType: sg.vertexType}
                    if ('source' == sg.vertexType) {
                        if ('db' == data.type) {
                            data.content.forEach(x => {
                                let row = {columnData: []}
                                dataSet.rows.push(row)
                                let sp = x.split(",")
                                sp.forEach(Y => {
                                    row.columnData.push(Y)
                                })
                            })

                            vertex.dataSet = dataSet
                            vertex.sourceType = 'db'
                        } else {
                            vertex.sourceType = 'merge'
                        }
                    }

                    body.vertexDTOList.push(vertex)
                }

                console.log('body--->', body)

                // line.forEach(x => {
                //     let p = {source: x.source.unique_id, target: x.target.unique_id}
                //     lines.push(p)
                // })

                request({
                    url: '/hi',
                    method: 'post',
                    data: body
                    // eslint-disable-next-line no-unused-vars
                }).then(response => {

                }).catch(() => {
                })


            },
            calculate() {

                /*连线集合*/
                let line = []

                /*端点集合*/
                let point = []

                let optionMap = {}


                let loadItem = (unique_id, ref) => {
                    let mapItemSource = optionMap[unique_id]
                    if (typeof mapItemSource == "undefined") {
                        mapItemSource = {unique_id: unique_id, parent: [], son: [], ref: ref}
                        optionMap[unique_id] = mapItemSource

                    }
                    return mapItemSource;
                }


                let cells = this.graph.getModel().cells
                for (let key in cells) {
                    let obb = cells[key]
                    if (obb.id.substring(0, 4) == 'cust') {

                        if ('action_edg' == obb.component) {
                            //线
                            if (obb.source == null || obb.target == null) {
                                this.graph.removeCells([obb])
                            } else {
                                let source = obb.source
                                let sourceId = source.unique_id
                                let mapItemSource = loadItem(sourceId, source)

                                let target = obb.target
                                let targetId = target.unique_id
                                let mapItemTarget = loadItem(targetId)

                                mapItemSource.son.push(mapItemTarget)

                                mapItemTarget.parent.push(mapItemSource)

                                line.push(obb)
                            }
                        } else {
                            point.push(obb)
                        }
                    }
                }


                if (line.length == 0) {
                    this.$message.error('流程至少需要两个端点一条连线')
                    return
                }


                this.sendDAG(line, point)

                // eslint-disable-next-line no-constant-condition
                if (true) {
                    return;
                }


                let rmParent = (array, x) => {
                    let na = []
                    array.forEach(p => {
                        if (p.unique_id != x.unique_id) {
                            na.push(p)
                        }
                    })
                    return na
                }


                let queue = []

                for (let key in optionMap) {
                    let sg = optionMap[key]
                    if (sg.parent.length == 0) {
                        sg.parentNum = 0
                        queue.push(sg)
                    }
                }

                if (queue.length == 0) {
                    this.$message.error('图中存在回环')
                    return
                }

                let groupMap = {}


                let hasLoop = true

                while (queue.length > 0) {
                    let one = queue.shift()

                    let gm = groupMap[one.parentNum]
                    if (typeof gm == "undefined") {
                        gm = []
                        groupMap[one.parentNum] = gm
                    }
                    gm.push(one)


                    one.son.forEach(x => {
                        x.parent = rmParent(x.parent, one)
                        if (x.parent.length == 0) {
                            hasLoop = false
                            x.parentNum = one.parentNum + 1
                            queue.push(x)
                        }
                    })


                }

                if (hasLoop) {
                    this.$message.error('图中存在回环')
                }


                for (let i = 0; i < Object.keys(groupMap).length; i++) {
                    let fo = groupMap[i]
                    if (typeof fo != "undefined") {
                        console.log('执行 ', fo)
                    }
                }


            },
            dbSubmit(id, data) {
                this.dataPool[id] = data
                this.$message.success("保存成功")
            },
            initDag() {
                let that = this

                // eslint-disable-next-line no-unused-vars
                mxGraph.prototype.getAllConnectionConstraints = function (terminal, source) {
                    if (terminal != null && terminal.shape != null) {
                        if (terminal.shape.stencil != null) {
                            if (terminal.shape.stencil.constraints != null) {
                                return terminal.shape.stencil.constraints;
                            }
                        } else if (terminal.shape.constraints != null) {
                            return terminal.shape.constraints;
                        }
                    }

                    return null;
                };

                // Defines the default constraints for all shapes
                mxShape.prototype.constraints = [new mxConnectionConstraint(new mxPoint(0.25, 0), true),
                    new mxConnectionConstraint(new mxPoint(0.5, 0), true),
                    new mxConnectionConstraint(new mxPoint(0.75, 0), true),
                    new mxConnectionConstraint(new mxPoint(0, 0.25), true),
                    new mxConnectionConstraint(new mxPoint(0, 0.5), true),
                    new mxConnectionConstraint(new mxPoint(0, 0.75), true),
                    new mxConnectionConstraint(new mxPoint(1, 0.25), true),
                    new mxConnectionConstraint(new mxPoint(1, 0.5), true),
                    new mxConnectionConstraint(new mxPoint(1, 0.75), true),
                    new mxConnectionConstraint(new mxPoint(0.25, 1), true),
                    new mxConnectionConstraint(new mxPoint(0.5, 1), true),
                    new mxConnectionConstraint(new mxPoint(0.75, 1), true)];

                // Edges have no connection points
                mxPolyline.prototype.constraints = null;


                if (!mxClient.isBrowserSupported()) {
                    // 判断是否支持mxgraph
                    mxUtils.error('Browser is not supported!', 200, false);
                } else {
                    // 再容器中创建图表


                    let MxGraph = mxGraph;
                    this.g1 = document.getElementById("g1")
                    this.graph = new MxGraph(this.g1);


                    /*获取全局父级*/
                    this.parent = this.graph.getDefaultParent();

                    let graph = this.graph


                    /* ----------- init ----------- */
                    this.initToolbar()
                    this.initKeyboardEvent()
                    /* ----------- init ----------- */


                    /* ----------- 设置配置值 ----------- */
                    /*鼠标滚动*/
                    mxEvent.addMouseWheelListener(function (evt, up) {
                        if (up) {
                            graph.zoomIn();
                        } else {
                            graph.zoomOut();
                        }

                        mxEvent.consume(evt);
                    });

                    this.graph.addListener(mxEvent.ESCAPE, () => {
                        that.cancel()
                    });


                    /*允许范围选取*/
                    new mxRubberband(graph);

                    /*禁止鼠标右键事件*/
                    mxEvent.disableContextMenu(this.g1);

                    //禁止大小缩放
                    mxGraph.prototype.setCellsResizable(false);

                    /*可连接*/
                    graph.setConnectable(true);


                    /*回退事务管理器*/
                    this.undoManager = new mxUndoManager();
                    let listener = function (sender, evt) {
                        that.undoManager.undoableEditHappened(evt.getProperty('edit'));
                    };
                    this.graph.getModel().addListener(mxEvent.UNDO, listener);
                    this.graph.getView().addListener(mxEvent.UNDO, listener);
                    /* ----------- 设置配置值 ----------- */


                    /* --------------- 设置事件 --------*/
                    /*点击*/
                    this.graph.addListener(mxEvent.CLICK, function (sender, evt) {
                        let cell = evt.getProperty('cell');
                        that.chooseCell(cell)

                    });

                    /*组件添加*/
                    this.graph.addListener(mxEvent.CELLS_ADDED, function (sender, evt) {
                        let cell = evt.getProperty('cells');
                        that.handleCellAdded(cell[0])
                    });


                    /*del按钮*/
                    let keyHandler = new mxKeyHandler(graph);
                    // eslint-disable-next-line no-unused-vars
                    keyHandler.bindKey(46, function (evt) {
                        if (graph.isEnabled()) {
                            graph.removeCells();
                        }
                    });

                    /* --------------- 设置事件 --------*/


                }
            },
            getCellInfo(cell) {
                let param = cell.id.split(";")
                let unique_id = param[1]
                let component = param[2]
                let ref = this.$refs[component]
                let info = {unique_id: unique_id, component: component, ref: ref}
                return info
            },
            handleCellAdded(cell) {


                if (typeof cell.unique_id == "undefined") {
                    cell.unique_id = this.uuidv4()
                }


                /*处理点*/
                if (cell.vertex == true) {
                    let uniqueId = cell.unique_id
                    let data = this.dataPool[uniqueId]
                    if (typeof data == "undefined") {

                        let info = this.getCellInfo(cell)
                        data = info.ref.initInnerData()
                        data.sourceValues = []
                        this.dataPool[uniqueId] = data
                    }
                }


                /*处理线*/
                if (cell.edge == true) {


                    cell.component = "action_edg"
                    cell.id = 'cust;' + this.uuidv4() + ';' + cell.component


                    let source = cell.source
                    let target = cell.target
                    if (source == null || target == null) {
                        this.$message.error('连接线必须有目标')
                        return;
                    }


                    let infoSource = this.getCellInfo(source)
                    let infoTarget = this.getCellInfo(target)
                    if (!infoTarget.ref.acceptCheck(infoSource.component)) {
                        this.$message.error('节点不符合连接条件')
                        this.graph.removeCells([cell])
                        return;
                    }


                    if (typeof source != "undefined" && typeof target != "undefined") {
                        let sourceData = this.dataPool[source.unique_id]
                        console.log('load source ----->', sourceData)

                        let targetData = this.dataPool[target.unique_id]
                        console.log('load target ----->', targetData)


                        let info = this.getCellInfo(target)
                        info.ref.bindSourceData(source.unique_id, sourceData, target.unique_id, targetData)
                    }

                }
            },
            chooseCell(cell) {

                if (typeof cell == "undefined") {
                    this.currentOptionPage = "un_choose"
                    return
                }

                this.choose_id = cell.unique_id

                this.currentOptionPage = cell.component

                let loadData = this.dataPool[cell.unique_id];
                let ref = this.$refs[cell.component]
                ref.load(cell.unique_id, loadData)


            },
            initKeyboardEvent() {

                let that = this
                document.onkeydown = (evt) => {

                    if (!evt) evt = event;

                    if (evt.ctrlKey && evt.keyCode === 90) {
                        evt.preventDefault()
                        that.undoManager.undo()

                    }

                    if (evt.ctrlKey && evt.keyCode === 89) {
                        evt.preventDefault()
                        that.undoManager.redo()

                    }

                    if (evt.ctrlKey && evt.keyCode === 65) {
                        evt.preventDefault()
                        that.graph.selectAll(null, true)
                    }


                }
            },
            addCell(toolItem, x, y) {
                const {width, height} = toolItem
                const styleObj = toolItem['style']

                const style = Object.keys(styleObj).map((attr) => `${attr}=${styleObj[attr]}`).join(';')
                const parent = this.graph.getDefaultParent()


                this.graph.getModel().beginUpdate()
                try {
                    let id = 'cust;' + this.uuidv4() + ';' + toolItem.component
                    let vertex = this.graph.insertVertex(parent, id, null, x, y, width, height, style)
                    vertex.title = toolItem['title']
                    vertex.component = toolItem['component']
                } finally {
                    this.graph.getModel().endUpdate()
                }
            },
            uuidv4() {
                return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, c =>
                    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
                );
            },
            initToolbar() {
                const domArray = this.$refs.toolItem

                if (!(domArray instanceof Array) || domArray.length <= 0) {
                    return
                }

                domArray.forEach((dom, domIndex) => {
                    const toolItem = this.toolbarGroupAll[domIndex]


                    const {width, height} = toolItem

                    const dropHandler = (graph, evt, cell, x, y) => {
                        this.addCell(toolItem, x, y)
                    }
                    const createDragPreview = () => {
                        const elt = document.createElement('div')

                        elt.style.border = '2px dotted black'
                        elt.style.width = `${width}px`
                        elt.style.height = `${height}px`
                        return elt
                    }

                    mxUtils.makeDraggable(dom, this.graph, dropHandler, createDragPreview(), 0, 0, false, true)
                })

                // this.toolbar = new mxToolbar(document.getElementById("tool_bar"));
                // this.toolbar.enabled = false
                // this.addToolBar('https://emojipedia.org/static/img/logo/emojipedia-logo-64.f24011dcde3f.png', 40, 40, 'shape=hexagon');
            },

        },
        mounted() {

            let that = this
            this.toolbarGroup.forEach(x => {
                x.graphArray.forEach(z => {
                    that.toolbarGroupAll.push(z)
                })
            })

            this.initDag()

        }
    }
</script>

<style>
    #g1 {
        border: 1px solid #ccc
    }

    .el-col {
        padding: 15px
    }

    div.mxRubberband {
        position: absolute;
        overflow: hidden;
        border-style: solid;
        border-width: 1px;
        border-color: #0000FF;
        background: #0077FF;
    }
</style>
