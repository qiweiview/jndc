package jndc_server.web_support.mapping;


import jndc_server.web_support.core.FrontProjectLoader;
import jndc_server.web_support.core.JNDCHttpRequest;
import jndc_server.web_support.core.WebMapping;
import jndc_server.web_support.model.data_transfer_object.ResponseMessage;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * singleton， thread unsafe
 */
public class DevelopDebugMapping {


    @WebMapping(path = UrlConstant.DevelopDebug.reloadFront)
    public HashMap run(JNDCHttpRequest jndcHttpRequest) {
        FrontProjectLoader.jndcStaticProject.reloadProject();
        HashMap objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("message", "success");
        return objectObjectHashMap;

    }

    @WebMapping(path = UrlConstant.DevelopDebug.getDeviceIp)
    public ResponseMessage getDeviceIp(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        InetAddress remoteAddress = jndcHttpRequest.getRemoteAddress();
        String hostAddress = remoteAddress.getHostAddress();
        responseMessage.setMessage("device ip:" + hostAddress);
        return responseMessage;


    }

}
