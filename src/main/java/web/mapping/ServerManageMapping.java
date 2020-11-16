package web.mapping;


import jndc.core.ChannelHandlerContextHolder;

import jndc.core.TcpServiceDescription;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store.DBWrapper;
import jndc.server.NDCServerConfigCenter;
import jndc.server.ServerPortBind;
import jndc.server.ServerPortBindContext;
import jndc.utils.AESUtils;
import jndc.utils.LogPrint;
import jndc.utils.UUIDSimple;
import web.core.JNDCHttpRequest;
import web.core.WebMapping;
import web.model.data_object.ManagementLoginUser;

import web.model.data_transfer_object.ResponseMessage;
import web.model.data_transfer_object.serviceBindDTO;
import web.model.view_object.ChannelContextVO;

import web.utils.AuthUtils;
import jndc.utils.JSONUtils;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * singleton， thread unsafe
 */
public class ServerManageMapping {


    /**
     * do login
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/login")
    public HashMap login(JNDCHttpRequest jndcHttpRequest) {
        HashMap objectObjectHashMap = new HashMap<>();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ManagementLoginUser managementLoginUser = JSONUtils.str2Object(s, ManagementLoginUser.class);
        if (AuthUtils.doLogin(managementLoginUser)) {
            InetAddress remoteAddress = jndcHttpRequest.getRemoteAddress();
            byte[] encode = AESUtils.encode(remoteAddress.getAddress());
            Base64.Encoder encoder = Base64.getEncoder();
            String s1 = encoder.encodeToString(encode);
            objectObjectHashMap.put("token", s1);

        } else {
            objectObjectHashMap.put("token", "403");
        }


        return objectObjectHashMap;

    }

    /**
     * get the active channel list
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/getServerChannelTable")
    public List<ChannelContextVO> getServerChannelTable(JNDCHttpRequest jndcHttpRequest) {

        List<ChannelContextVO> list = new ArrayList<>();
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();
        channelHandlerContextHolders.forEach(x -> {
            list.add(ChannelContextVO.of(x));
        });


        return list;

    }


    /**
     * close channel by id
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/closeChannelByServer")
    public ResponseMessage closeChannelByServer(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ChannelContextVO channelContextVO = JSONUtils.str2Object(s, ChannelContextVO.class);
        String id = channelContextVO.getId();

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        bean.unRegisterServiceProvider(id);

        return new ResponseMessage();

    }


    /**
     * close channel by id
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/getServiceList")
    public List<TcpServiceDescription> getServiceList(JNDCHttpRequest jndcHttpRequest) {


        List<TcpServiceDescription> tcpServiceDescriptions = new ArrayList<>();

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();

        channelHandlerContextHolders.forEach(x -> {
            tcpServiceDescriptions.addAll(x.getTcpServiceDescriptions());
        });
        return tcpServiceDescriptions;

    }

    /**
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/getServerPortList")
    public List<ServerPortBind> getServerPortList(JNDCHttpRequest jndcHttpRequest) {
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        List<ServerPortBind> serverPortBinds = dbWrapper.listAll();
        return serverPortBinds;

    }

    /**
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/createPortMonitoring")
    public ResponseMessage createPortMonitoring(JNDCHttpRequest jndcHttpRequest) {


        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServerPortBind channelContextVO = JSONUtils.str2Object(s, ServerPortBind.class);
        channelContextVO.setPortEnable(0);
        channelContextVO.setId(UUIDSimple.id());

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);

        List<ServerPortBind> serverPortBinds = dbWrapper.customQuery("select * from server_port_bind where port=?", channelContextVO.getPort());

        ResponseMessage responseMessage = new ResponseMessage();
        if (serverPortBinds.size() > 0) {
            responseMessage.error();
            responseMessage.setMessage("端口 " + channelContextVO.getPort() + " 已被占用");
            return responseMessage;
        }


        dbWrapper.insert(channelContextVO);
        return responseMessage;

    }


    /**
     * do service bind
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/doServiceBind")
    public ResponseMessage doServiceBind(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        serviceBindDTO channelContextVO = JSONUtils.str2Object(s, serviceBindDTO.class);

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        ServerPortBind serverPortBind = dbWrapper.customQuerySingle("select * from server_port_bind where id=?", channelContextVO.getServerPortId());
        if (serverPortBind == null) {
            responseMessage.error();
            responseMessage.setMessage("端口监听不存在");
            return responseMessage;
        }

        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();
        channelHandlerContextHolders.forEach(x -> {
            if (atomicBoolean.get()) {
                List<TcpServiceDescription> tcpServiceDescriptions = x.getTcpServiceDescriptions();
                tcpServiceDescriptions.forEach(y -> {
                    TcpServiceDescription y1 = y;
                    if (y.getId().equals(channelContextVO.getServiceId())) {
                        String s1 = y1.getIp() + ":" + y1.getPort();
                        serverPortBind.setRouteTo(s1);
                        //set true
                        serverPortBind.setPortEnable(1);
                        dbWrapper.updateByPrimaryKey(serverPortBind);
                        bean.addTCPRouter(serverPortBind.getPort(),y);

                        //bind just once
                        atomicBoolean.set(false);
                    }
                });
            }
        });

        if (atomicBoolean.get()){
            responseMessage.error();
            responseMessage.setMessage("未找到对应编号服务："+channelContextVO.getServiceId());
            return responseMessage;
        }

        return responseMessage;

    }


    /**
     * do service bind
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/stopServiceBind")
    public ResponseMessage stopServiceBind(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        serviceBindDTO channelContextVO = JSONUtils.str2Object(s, serviceBindDTO.class);

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        ServerPortBind serverPortBind = dbWrapper.customQuerySingle("select * from server_port_bind where id=?", channelContextVO.getServerPortId());
        if (serverPortBind == null) {
            responseMessage.error();
            responseMessage.setMessage("端口监听不存在");
            return responseMessage;
        }

        int port = serverPortBind.getPort();
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        Map<Integer, ServerPortBindContext> tcpRouter = bean.getTcpRouter();
        ServerPortBindContext serverPortBindContext = tcpRouter.get(port);
        if (serverPortBindContext!=null){
            serverPortBindContext.releaseRelatedResources();
            tcpRouter.remove(port);

        }else {
            serverPortBind.setPortEnable(0);
            serverPortBind.setRouteTo(null);
            dbWrapper.updateByPrimaryKey(serverPortBind);
        }


        return responseMessage;

    }


}
