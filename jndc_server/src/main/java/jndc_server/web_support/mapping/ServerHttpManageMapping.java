package jndc_server.web_support.mapping;


import io.netty.util.internal.StringUtil;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.PageResult;
import jndc.utils.BeanUtils;
import jndc.utils.JSONUtils;
import jndc.utils.UUIDSimple;
import jndc_server.web_support.core.JNDCHttpRequest;
import jndc_server.web_support.core.WebMapping;
import jndc_server.web_support.http_module.HostRouter;
import jndc_server.web_support.model.data_object.HttpHostRoute;
import jndc_server.web_support.model.data_transfer_object.HostRouteDTO;
import jndc_server.web_support.model.data_transfer_object.ResponseMessage;
import jndc_server.web_support.model.view_object.HttpHostRouteVO;

import jndc_server.web_support.model.view_object.PageListVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * singleton， thread unsafe
 */
public class ServerHttpManageMapping {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @WebMapping(path = "/saveHostRouteRule")
    public ResponseMessage saveHostRouteRule(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HostRouteDTO hostRouteDTO = JSONUtils.str2Object(s, HostRouteDTO.class);
        HttpHostRoute httpHostRoute = HttpHostRoute.of(hostRouteDTO);
        httpHostRoute.setId(UUIDSimple.id());

        if (hostRouteDTO.getHostKeyWord().length() > 50 || hostRouteDTO.getFixedResponse().length() > 500) {
            responseMessage.error();
            responseMessage.setMessage("字段长度超出限制");
            return responseMessage;
        }

        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);

        if (checkHostKeyExist(httpHostRoute.getHostKeyWord())) {
            responseMessage.error();
            responseMessage.setMessage("关键字\"" + httpHostRoute.getHostKeyWord() + "\" 已存在");
            return responseMessage;
        }




        dbWrapper.insert(httpHostRoute);
        HostRouter hostRouter = UniqueBeanManage.getBean(HostRouter.class);
        hostRouter.addRule(httpHostRoute);
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

        Integer count = dbWrapper.customQuerySingleValue("count", "select count(*) count from http_host_route where hostKeyWord= ?", Integer.class,hostKey);
        return count>0;
    }

    @WebMapping(path = "/updateHostRouteRule")
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


        if (hostRouteDTO.getHostKeyWord().length() > 50 || hostRouteDTO.getFixedResponse().length() > 500) {
            responseMessage.error();
            responseMessage.setMessage("字段长度超出限制");
            return responseMessage;
        }
        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);

        if (!oldRule.getHostKeyWord().equals(newRule.getHostKeyWord())&&checkHostKeyExist(newRule.getHostKeyWord())) {
            responseMessage.error();
            responseMessage.setMessage("关键字\"" + newRule.getHostKeyWord() + "\" 已存在");
            return responseMessage;
        }

        dbWrapper.updateByPrimaryKey(newRule);

        HostRouter hostRouter = UniqueBeanManage.getBean(HostRouter.class);
        hostRouter.updateRule(oldRule, newRule);
        return responseMessage;

    }

    @WebMapping(path = "/deleteHostRouteRule")
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
        HostRouter hostRouter = UniqueBeanManage.getBean(HostRouter.class);
        hostRouter.removeRule(oldRule);

        return responseMessage;
    }

    @WebMapping(path = "/listHostRouteRule")
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
            condition = "and hostKeyWord like '%" + hostRouteDTO.getHostKeyWord() + "%'";
        }
        PageResult<HttpHostRoute> httpHostRoutePageResult = dbWrapper.customQueryByPage("select * from http_host_route where 1=1 " + condition, hostRouteDTO.getPage(), hostRouteDTO.getRows());


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
