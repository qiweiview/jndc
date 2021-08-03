package jndc_client.http_support;

import jndc.core.UniqueBeanManage;
import jndc.http_support.WebMapping;
import jndc.http_support.model.NettyRequest;
import jndc.http_support.model.UrlQueryKV;
import jndc_client.core.ClientServiceProvider;
import jndc_client.core.ClientTCPDataHandle;
import jndc_client.core.JNDCClientConfigCenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManagementMapping {

    @WebMapping(value = "/", type = WebMapping.RESPONSE_TYPE.HTML)
    public String homePage(NettyRequest nettyRequest) {

        return "<html><body><h3>客户端管理</h3>" +
                "<a href=\"/listServiceProvider\" target=\"listServiceProvider\">服务列表</a><br>" +
                "<a href=\"/killConnection?pId=123&tId=123\" target=\"killConnection\">中断连接</a>" +
                "</body></html>";

    }


    /**
     * 服务提供者列表
     *
     * @param nettyRequest
     * @return
     */
    @WebMapping(value = "/listServiceProvider", type = WebMapping.RESPONSE_TYPE.JSON)
    public Object listServiceProvider(NettyRequest nettyRequest) {
        JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
        Map<String, ClientServiceProvider> portProtectorMap = bean.getPortProtectorMap();
        List<ClientServiceProvider> list = new ArrayList<>();
        portProtectorMap.forEach((k, v) -> {
            list.add(v);
        });
        return list;

    }


    /**
     * 中断连接
     *
     * @param nettyRequest
     * @return
     */
    @WebMapping(value = "/killConnection", type = WebMapping.RESPONSE_TYPE.JSON)
    public Object killConnection(NettyRequest nettyRequest) {
        Map<String, UrlQueryKV> queryMap = nettyRequest.getQueryMap();
        UrlQueryKV pId = queryMap.get("pId");
        UrlQueryKV tId = queryMap.get("tId");

        if (pId == null || tId == null) {
            return "need query parameter \"pId\" and \"tId\" ";
        }

        JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
        Map<String, ClientServiceProvider> portProtectorMap = bean.getPortProtectorMap();

        final ClientServiceProvider[] clientServiceProvider = {null};
        portProtectorMap.forEach((k, v) -> {
            if (pId.getValue().equals(v.getPId())) {
                clientServiceProvider[0] = v;
            }
        });

        if (clientServiceProvider[0] == null) {
            return "can  not found the service provider for pId:" + pId.getValue();
        }

        String value = tId.getValue();
        Map<String, ClientTCPDataHandle> faceTCPMap = clientServiceProvider[0].getFaceTCPMap();
        int[] ef = {0};
        faceTCPMap.forEach((k, v) -> {
            if (value.equals(v.getTId()) || "*".equals(value)) {
                v.releaseRelatedResources();
                ef[0]++;
            }

        });


        return "kill success,effect " + ef[0];
    }


}
