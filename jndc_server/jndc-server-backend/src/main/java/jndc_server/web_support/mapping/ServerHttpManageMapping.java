package jndc_server.web_support.mapping;


import io.netty.util.internal.StringUtil;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.PageResult;
import jndc.utils.BeanUtils;
import jndc.utils.JSONUtils;
import jndc.utils.StringUtils4V;
import jndc.utils.UUIDSimple;
import jndc.web_support.core.JNDCHttpRequest;
import jndc.web_support.core.WebMapping;
import jndc.web_support.model.dto.ResponseMessage;
import jndc_server.web_support.http_module.HostRouterComponent;
import jndc_server.web_support.model.d_o.HttpHostRoute;
import jndc_server.web_support.model.dto.HostRouteDTO;
import jndc_server.web_support.model.vo.HttpHostRouteVO;
import jndc_server.web_support.model.vo.PageListVO;
import jndc_server.web_support.utils.ServerUrlConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * singleton， thread unsafe
 */
public class ServerHttpManageMapping {

    private static final Pattern HOST_KEYWORD_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{1,50}$");

    private ResponseMessage validateAndNormalizeRule(HostRouteDTO hostRouteDTO, HttpHostRoute httpHostRoute) {
        ResponseMessage responseMessage = new ResponseMessage();
        if (hostRouteDTO == null) {
            responseMessage.error();
            responseMessage.setMessage("请求参数不能为空");
            return responseMessage;
        }

        if (StringUtils4V.isBlank(hostRouteDTO.getHostKeyWord())) {
            responseMessage.error();
            responseMessage.setMessage("包含字符不能为空");
            return responseMessage;
        }

        if (!HOST_KEYWORD_PATTERN.matcher(hostRouteDTO.getHostKeyWord()).matches()) {
            responseMessage.error();
            responseMessage.setMessage("路由键仅支持字母、数字、下划线和短横线");
            return responseMessage;
        }

        if (hostRouteDTO.getRouteType() < 0 || hostRouteDTO.getRouteType() > 2) {
            responseMessage.error();
            responseMessage.setMessage("不支持路由类型");
            return responseMessage;
        }

        if (!("http://".equals(hostRouteDTO.getForwardProtocol()) || "https://".equals(hostRouteDTO.getForwardProtocol()))) {
            responseMessage.error();
            responseMessage.setMessage("不支持协议类型");
            return responseMessage;
        }

        String fixedResponse = hostRouteDTO.getFixedResponse();
        if (fixedResponse == null) {
            fixedResponse = "";
            hostRouteDTO.setFixedResponse(fixedResponse);
            httpHostRoute.setFixedResponse(fixedResponse);
        }

        if (fixedResponse.length() > 10000) {
            responseMessage.error();
            responseMessage.setMessage("固定内容长度超出限制" + fixedResponse.length());
            return responseMessage;
        }

        if (httpHostRoute.forwardType()) {
            if (hostRouteDTO.getForwardPort() < 1 || hostRouteDTO.getForwardPort() > 65535) {
                responseMessage.error();
                responseMessage.setMessage("转发端口不合法");
                return responseMessage;
            }
            httpHostRoute.setForwardHost("127.0.0.1");
        }

        return null;
    }


    @WebMapping(path = ServerUrlConstant.ServerHttp.saveHostRouteRule)
    public ResponseMessage saveHostRouteRule(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HostRouteDTO hostRouteDTO = JSONUtils.str2Object(s, HostRouteDTO.class);
        HttpHostRoute httpHostRoute = HttpHostRoute.of(hostRouteDTO);
        httpHostRoute.setId(UUIDSimple.id());

        ResponseMessage validation = validateAndNormalizeRule(hostRouteDTO, httpHostRoute);
        if (validation != null) {
            return validation;
        }

        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);

        if (checkHostKeyExist(httpHostRoute.getHostKeyWord())) {
            responseMessage.error();
            responseMessage.setMessage("关键字\"" + httpHostRoute.getHostKeyWord() + "\" 已存在");
            return responseMessage;
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

    @WebMapping(path = ServerUrlConstant.ServerHttp.updateHostRouteRule)
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

        ResponseMessage validation = validateAndNormalizeRule(hostRouteDTO, newRule);
        if (validation != null) {
            return validation;
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

    @WebMapping(path = ServerUrlConstant.ServerHttp.deleteHostRouteRule)
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

    @WebMapping(path = ServerUrlConstant.ServerHttp.listHostRouteRule)
    public Object listHostRouteRule(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HostRouteDTO hostRouteDTO = JSONUtils.str2Object(s, HostRouteDTO.class);

        DBWrapper<HttpHostRoute> dbWrapper = DBWrapper.getDBWrapper(HttpHostRoute.class);
        StringBuilder sqlBuilder = new StringBuilder("select * from http_host_route where 1=1");
        List<Object> params = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(hostRouteDTO.getHostKeyWord())) {
            if (hostRouteDTO.getHostKeyWord().matches("\\d+")) {
                sqlBuilder.append(" and (host_key_word like ? or forward_port=?)");
                params.add("%" + hostRouteDTO.getHostKeyWord() + "%");
                params.add(Integer.parseInt(hostRouteDTO.getHostKeyWord()));
            } else {
                sqlBuilder.append(" and host_key_word like ?");
                params.add("%" + hostRouteDTO.getHostKeyWord() + "%");
            }
        }
        String order = " order by host_key_word";
        PageResult<HttpHostRoute> httpHostRoutePageResult = dbWrapper.customQueryByPage(sqlBuilder.toString() + order, hostRouteDTO.getPage(), hostRouteDTO.getRows(), params.toArray(new Object[0]));


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
