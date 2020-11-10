package web.mapping;


import jndc.core.ChannelHandlerContextHolder;
import jndc.core.ServerPortProtector;
import jndc.core.UniqueBeanManage;
import jndc.server.NDCServerConfigCenter;
import jndc.utils.AESUtils;
import web.core.JNDCHttpRequest;
import web.core.WebMapping;
import web.model.data_object.ManagementLoginUser;
import web.model.data_transfer_object.PortDTO;
import web.model.data_transfer_object.ResponseMessage;
import web.model.view_object.ChannelContextVO;
import web.model.view_object.FacePortVO;
import web.utils.AuthUtils;
import web.utils.JSONUtils;

import java.net.InetAddress;
import java.util.*;

/**
 * singleton， thread unsafe
 */
public class ServerManageMapping {

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


    @WebMapping(path = "/getServerMappingList")
    public List<FacePortVO> getServerMappingList(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        HashMap hashMap = JSONUtils.str2Object(s, HashMap.class);

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<FacePortVO> facePortVOS = new ArrayList<>();
        bean.getPortProtectorMap().forEach((k, v) -> {
            facePortVOS.add(FacePortVO.of(v.getRegisterMessage()));
        });


        return facePortVOS;

    }


    @WebMapping(path = "/shutDownServerPort")
    public ResponseMessage shutDownServerPort(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        PortDTO portDTO = JSONUtils.str2Object(s, PortDTO.class);

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ServerPortProtector serverPortProtector = bean.getPortProtectorMap().get(portDTO.getPort());
        if (serverPortProtector != null) {
            serverPortProtector.releaseObject();
        }

        return new ResponseMessage();
    }




    /* -----------------channelContext-------------- */


    @WebMapping(path = "/closeChannelByServer")
    public ResponseMessage closeChannelByServer(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ChannelContextVO channelContextVO = JSONUtils.str2Object(s, ChannelContextVO.class);
        String channelId = channelContextVO.getChannelId();

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        Map<Integer, ChannelHandlerContextHolder> contextHolderMap = bean.getContextHolderMap();
        contextHolderMap.forEach((k, v) -> {
            if (channelId.equals(v.getId())) {
                bean.shutDownChannelHandlerContextHolder(v);
            }
        });


        return new ResponseMessage();

    }

    @WebMapping(path = "/getServerPortList")
    public List<FacePortVO> getServerPortList(JNDCHttpRequest jndcHttpRequest) {
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<FacePortVO> contextVOS = new ArrayList<>();
        bean.getContextHolderMap().keySet().forEach(x -> {
            FacePortVO facePortVO = new FacePortVO();
            facePortVO.setServerPort(x);
            contextVOS.add(facePortVO);
        });
        return contextVOS;

    }


    @WebMapping(path = "/getServerChannelTable")
    public List<ChannelContextVO> getServerChannelTable(JNDCHttpRequest jndcHttpRequest) {


        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);


        Map<String, ChannelContextVO> contextVOHashMap = new HashMap<>();
        List<ChannelContextVO> contextVOS = new ArrayList<>();
        Map<Integer, ChannelHandlerContextHolder> contextHolderMap = bean.getContextHolderMap();

        contextHolderMap.forEach((k, v) -> {
            String id = v.getId();

            ChannelContextVO of = ChannelContextVO.of(k, v.getChannelHandlerContext());
            of.setChannelId(id);


            ChannelContextVO channelContextVO = contextVOHashMap.get(of.uniqueTag());
            if (channelContextVO == null) {
                channelContextVO = of;
                contextVOHashMap.put(channelContextVO.uniqueTag(), channelContextVO);
                contextVOS.add(channelContextVO);
            } else {
                channelContextVO.mergeUsedServerPort(of.getUsedServerPorts());
            }
        });
        return contextVOS;

    }


}
