package web.mapping;


import web.core.FrontProjectLoader;
import web.core.JNDCHttpRequest;
import web.core.WebMapping;
import web.model.data_transfer_object.ResponseMessage;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * singleton， thread unsafe
 */
public class DevelopDebugMapping {



    @WebMapping(path = "/reloadFront")
    public HashMap run(JNDCHttpRequest jndcHttpRequest){
        FrontProjectLoader.jndcStaticProject.reloadProject();
        HashMap objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("message","success");
        return objectObjectHashMap;

    }

    @WebMapping(path = "/getDeviceIp")
    public ResponseMessage getDeviceIp(JNDCHttpRequest jndcHttpRequest){
        ResponseMessage responseMessage = new ResponseMessage();

        InetAddress remoteAddress = jndcHttpRequest.getRemoteAddress();
        String hostAddress = remoteAddress.getHostAddress();
        responseMessage.setMessage("device ip:"+hostAddress);
        return responseMessage;


    }

}
