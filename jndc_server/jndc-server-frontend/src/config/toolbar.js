import mxgraph from "@/config/mxgraph";
import Vue from 'vue';

import agg_vertex from "@/views/item_detail/agg/agg_vertex";
import average_vertex from "@/views/item_detail/agg/average_vertex";
import max_vertex from "@/views/item_detail/agg/max_vertex";
import merge_vertex from "@/views/item_detail/agg/merge_vertex";
import min_vertex from "@/views/item_detail/agg/min_vertex";
import sum_vertex from "@/views/item_detail/agg/sum_vertex";


import db_vertex from "@/views/item_detail/data_source/db_vertex";

import action_edg from "@/views/item_detail/edg_operation/action_edg";


Vue.component('agg_vertex', agg_vertex)
Vue.component('average_vertex', average_vertex)
Vue.component('max_vertex', max_vertex)
Vue.component('merge_vertex', merge_vertex)
Vue.component('min_vertex', min_vertex)
Vue.component('sum_vertex', sum_vertex)

Vue.component('db_vertex', db_vertex)
Vue.component('action_edg', action_edg)

let toolbarGroup = [
    {
        name: "数据源",
        dataArray: [
            {
                icon: '/tool_bar_icon/database.png',
                component: 'db_vertex',
                title: '数据',
            }
        ],
        graphArray: []
    },
    {
        name: "聚合操作",
        dataArray: [
            {
                icon: '/tool_bar_icon/agg.png',
                component: 'agg_vertex',
                title: '聚合',
            },
            {
                icon: '/tool_bar_icon/max.png',
                component: 'max_vertex',
                title: '最大值',
            },
            {
                icon: '/tool_bar_icon/min.png',
                component: 'min_vertex',
                title: '最小值',
            },
            {
                icon: '/tool_bar_icon/sum.png',
                component: 'sum_vertex',
                title: '求和',
            },
            {
                icon: '/tool_bar_icon/average.png',
                component: 'average_vertex',
                title: '平均值',
            },
            {
                icon: '/tool_bar_icon/merge.png',
                component: 'merge_vertex',
                title: '合并',
            }
        ],
        graphArray: []
    }

]


const width_config = 128
const height_config = 128

for (let i = 0; i < toolbarGroup.length; i++) {
    let graphArray = toolbarGroup[i].graphArray
    let innerArray = toolbarGroup[i].dataArray
    for (let j = 0; j < innerArray.length; j++) {
        let fs = innerArray[j]
        let sg = {
            icon: fs.icon,
            title: fs.title,
            width: width_config,
            height: height_config,
            component: fs.component,
            style: {
                fillColor: 'transparent',
                strokeColor: 'transparent',
                strokeWidth: '1',
                shape: mxgraph.mxConstants.SHAPE_IMAGE,
                align: mxgraph.mxConstants.ALIGN_CENTER,
                verticalAlign: mxgraph.mxConstants.ALIGN_CENTER,
                imageAlign: mxgraph.mxConstants.ALIGN_CENTER,
                imageVerticalAlign: mxgraph.mxConstants.ALIGN_CENTER,
                width: width_config,
                height: height_config,
                image: fs.icon
            }
        }
        graphArray.push(sg)
    }


}

export default toolbarGroup
