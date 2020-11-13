package web.mapping;


import jndc.core.ChannelHandlerContextHolder;

import jndc.core.TcpServiceDescription;
import jndc.core.UniqueBeanManage;
import jndc.server.NDCServerConfigCenter;
import jndc.utils.AESUtils;
import web.core.JNDCHttpRequest;
import web.core.WebMapping;
import web.model.data_object.ManagementLoginUser;

import web.model.data_transfer_object.ResponseMessage;
import web.model.view_object.ChannelContextVO;

import web.utils.AuthUtils;
import jndc.utils.JSONUtils;

import java.net.InetAddress;
import java.util.*;

/**
 * singleton， thread unsafe
 */
public class ServerManageMapping {


    /**
     * do login
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
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = "/getServiceList")
    public List<TcpServiceDescription>  getServiceList(JNDCHttpRequest jndcHttpRequest) {


        List<TcpServiceDescription> tcpServiceDescriptions=new ArrayList<>();

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();

        channelHandlerContextHolders.forEach(x->{
            tcpServiceDescriptions.addAll(x.getTcpServiceDescriptions());
        });
        return tcpServiceDescriptions;

    }

//
//
//    @WebMapping(path = "/getServerMappingList")
//    public List<FacePortVO> getServerMappingList(JNDCHttpRequest jndcHttpRequest) {
//        byte[] body = jndcHttpRequest.getBody();
//        String s = new String(body);
//        HashMap hashMap = JSONUtils.str2Object(s, HashMap.class);
//
//        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
//        List<FacePortVO> facePortVOS = new ArrayList<>();
//        bean.getPortProtectorMap().forEach((k, v) -> {
//            facePortVOS.add(FacePortVO.of(v.getRegisterMessage()));
//        });
//
//
//        return facePortVOS;
//
//    }
//
//
//    @WebMapping(path = "/shutDownServerPort")
//    public ResponseMessage shutDownServerPort(JNDCHttpRequest jndcHttpRequest) {
//        byte[] body = jndcHttpRequest.getBody();
//        String s = new String(body);
//        PortDTO portDTO = JSONUtils.str2Object(s, PortDTO.class);
//
//        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
//        ServerPortProtector serverPortProtector = bean.getPortProtectorMap().get(portDTO.getPort());
//        if (serverPortProtector != null) {
//            serverPortProtector.releaseObject();
//        }
//
//        return new ResponseMessage();
//    }
//
//
//
//
//    /* -----------------channelContext-------------- */
//
//
//    @WebMapping(path = "/closeChannelByServer")
//    public ResponseMessage closeChannelByServer(JNDCHttpRequest jndcHttpRequest) {
//        byte[] body = jndcHttpRequest.getBody();
//        String s = new String(body);
//        ChannelContextVO channelContextVO = JSONUtils.str2Object(s, ChannelContextVO.class);
//        String channelId = channelContextVO.getChannelId();
//
//        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
//        Map<Integer, ChannelHandlerContextHolder> contextHolderMap = bean.getContextHolderMap();
//        contextHolderMap.forEach((k, v) -> {
//            if (channelId.equals(v.getId())) {
//                bean.shutDownChannelHandlerContextHolder(v);
//            }
//        });
//
//
//        return new ResponseMessage();
//
//    }
//
//    @WebMapping(path = "/getServerPortList")
//    public List<FacePortVO> getServerPortList(JNDCHttpRequest jndcHttpRequest) {
//        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
//        List<FacePortVO> contextVOS = new ArrayList<>();
//        bean.getContextHolderMap().keySet().forEach(x -> {
//            FacePortVO facePortVO = new FacePortVO();
//            facePortVO.setServerPort(x);
//            contextVOS.add(facePortVO);
//        });
//        return contextVOS;
//
//    }
//
//


}
