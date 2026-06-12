package jndc_server.web_support.mapping;


import jndc.web_support.core.JNDCHttpRequest;
import jndc.web_support.core.WebMapping;
import jndc.web_support.model.dto.ResponseMessage;
import jndc_server.web_support.utils.ServerUrlConstant;

import java.net.InetAddress;

/**
 * singleton， thread unsafe
 */
public class DevelopDebugMapping {

    @WebMapping(path = ServerUrlConstant.DevelopDebug.getDeviceIp)
    public ResponseMessage getDeviceIp(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        InetAddress remoteAddress = jndcHttpRequest.getRemoteAddress();
        String hostAddress = remoteAddress.getHostAddress();
        responseMessage.setMessage("device ip:" + hostAddress);
        return responseMessage;


    }

}
