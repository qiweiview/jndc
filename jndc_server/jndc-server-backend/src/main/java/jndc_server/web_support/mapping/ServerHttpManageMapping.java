package jndc_server.web_support.mapping;


import io.netty.util.internal.StringUtil;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.PageResult;
import jndc.utils.BeanUtils;
import jndc.utils.JSONUtils;
import jndc.utils.StringUtils4V;
import jndc.utils.UUIDSimple;
import jndc_server.web_support.core.JNDCHttpRequest;
import jndc_server.web_support.core.WebMapping;
import jndc_server.web_support.http_module.HostRouterComponent;
import jndc_server.web_support.model.d_o.HttpHostRoute;
import jndc_server.web_support.model.dto.HostRouteDTO;
import jndc_server.web_support.model.dto.ResponseMessage;
import jndc_server.web_support.model.vo.HttpHostRouteVO;
import jndc_server.web_support.model.vo.PageListVO;

import java.util.ArrayList;
import java.util.List;

/**
 * singleton， thread unsafe
 */
public class ServerHttpManageMapping {


    @WebMapping(path = UrlConstant.ServerHttp.saveHostRouteRule)
    public ResponseMessage saveHostRouteRule(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HostRouteDTO hostRouteDTO = JSONUtils.str2Object(s, HostRouteDTO.class);
        if (StringUtils4V.isBlank( hostRouteDTO.getHostKeyWord())){
            responseMessage.error();
            responseMessage.setMessage("包含字符不能为空");
            return responseMessage;
        }
        HttpHostRoute httpHostRoute = HttpHostRoute.of(hostRouteDTO);
        httpHostRoute.setId(UUIDSimple.id());

        if (!("http://".equals(hostRouteDTO.getForwardProtocol())||"https://".equals(hostRouteDTO.getForwardProtocol()))) {
            responseMessage.error();
            responseMessage.setMessage("不支持协议类型");
            return responseMessage;
        }

        if (hostRouteDTO.getHostKeyWord().length() > 50 ) {
            responseMessage.error();
            responseMessage.setMessage("路由键值长度超出限制："+hostRouteDTO.getHostKeyWord().length());
            return responseMessage;
        }

        if (hostRouteDTO.getFixedResponse().length() > 10000) {
            responseMessage.error();
            responseMessage.setMessage("固定内容长度超出限制" + hostRouteDTO.getFixedResponse().length());
            return responseMessage;
        }

        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);

        if (checkHostKeyExist(httpHostRoute.getHostKeyWord())) {
            responseMessage.error();
            responseMessage.setMessage("关键字\"" + httpHostRoute.getHostKeyWord() + "\" 已存在");
            return responseMessage;
        }



        if (httpHostRoute.forwardType()){
            httpHostRoute.setForwardHost("127.0.0.1");
        }

        dbWrapper.insert(httpHostRoute);
        HostRouterComponent hostRouterComponent = UniqueBeanManage.getBean(HostRouterComponent.class);
        hostRouterComponent.addRule(httpHostRoute);
        return responseMessage;

    }

    /**
     * private method
     *
     * @param id
     * @return
     */
    private HttpHostRoute findHttpHostRouteById(String id) {
        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);
        HttpHostRoute httpHostRoute = dbWrapper.customQuerySingle("select  * from http_host_route where id=?", id);
        return httpHostRoute;

    }


    public boolean checkHostKeyExist(String hostKey){
        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);

        Number count = dbWrapper.customQuerySingleValue("count", "select count(*) count from http_host_route where host_key_word= ?", Number.class, hostKey);
        return count.intValue() > 0;
    }

    @WebMapping(path = UrlConstant.ServerHttp.updateHostRouteRule)
    public ResponseMessage updateHostRouteRule(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HostRouteDTO hostRouteDTO = JSONUtils.str2Object(s, HostRouteDTO.class);
        HttpHostRoute newRule = HttpHostRoute.of(hostRouteDTO);


        HttpHostRoute oldRule = findHttpHostRouteById(hostRouteDTO.getId());
        if (oldRule == null) {
            responseMessage.error();
            responseMessage.setMessage("规则不存在");
            return responseMessage;
        }

        newRule.setId(oldRule.getId());



        if (hostRouteDTO.getHostKeyWord().length() > 50 ) {
            responseMessage.error();
            responseMessage.setMessage("路由键值长度超出限制："+hostRouteDTO.getHostKeyWord().length());
            return responseMessage;
        }

        if (hostRouteDTO.getFixedResponse().length() > 10000) {
            responseMessage.error();
            responseMessage.setMessage("固定内容长度超出限制" + hostRouteDTO.getFixedResponse().length());
            return responseMessage;
        }

        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);

        if (!oldRule.getHostKeyWord().equals(newRule.getHostKeyWord())&&checkHostKeyExist(newRule.getHostKeyWord())) {
            responseMessage.error();
            responseMessage.setMessage("关键字\"" + newRule.getHostKeyWord() + "\" 已存在");
            return responseMessage;
        }

        dbWrapper.updateByPrimaryKey(newRule);

        HostRouterComponent hostRouterComponent = UniqueBeanManage.getBean(HostRouterComponent.class);
        hostRouterComponent.updateRule(oldRule, newRule);
        return responseMessage;

    }

    @WebMapping(path = UrlConstant.ServerHttp.deleteHostRouteRule)
    public ResponseMessage deleteHostRouteRule(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HostRouteDTO hostRouteDTO = JSONUtils.str2Object(s, HostRouteDTO.class);

        HttpHostRoute oldRule = findHttpHostRouteById(hostRouteDTO.getId());

        if (oldRule == null) {
            responseMessage.error();
            responseMessage.setMessage("规则不存在");
            return responseMessage;
        }


        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);

        dbWrapper.deleteByPrimaryKey(oldRule);
        HostRouterComponent hostRouterComponent = UniqueBeanManage.getBean(HostRouterComponent.class);
        hostRouterComponent.removeRule(oldRule);

        return responseMessage;
    }

    @WebMapping(path = UrlConstant.ServerHttp.listHostRouteRule)
    public Object listHostRouteRule(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HostRouteDTO hostRouteDTO = JSONUtils.str2Object(s, HostRouteDTO.class);

        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);
        String condition;
        if (StringUtil.isNullOrEmpty(hostRouteDTO.getHostKeyWord())) {
            condition = "";
        } else {
            condition = "and (hostKeyWord like '%" + hostRouteDTO.getHostKeyWord() + "%' or forwardPort=" + hostRouteDTO.getHostKeyWord() + ")";
        }
        String order = " order by host_key_word";
        PageResult<HttpHostRoute> httpHostRoutePageResult = dbWrapper.customQueryByPage("select * from http_host_route where 1=1 " + condition + order, hostRouteDTO.getPage(), hostRouteDTO.getRows());


        List<HttpHostRouteVO> httpHostRouteVOS = new ArrayList<>();
        httpHostRoutePageResult.getData().forEach(x -> {
            HttpHostRouteVO httpHostRouteVO1 = BeanUtils.copyValue(x, HttpHostRouteVO.class);
            httpHostRouteVOS.add(httpHostRouteVO1);
        });


        PageListVO<HttpHostRouteVO> channelContextCloseRecordPageListVO = new PageListVO<>();
        channelContextCloseRecordPageListVO.setPage(hostRouteDTO.getPage());
        channelContextCloseRecordPageListVO.setRows(hostRouteDTO.getRows());
        channelContextCloseRecordPageListVO.setData(httpHostRouteVOS);
        channelContextCloseRecordPageListVO.setTotal(httpHostRoutePageResult.getTotal());

        return channelContextCloseRecordPageListVO;

    }

}
