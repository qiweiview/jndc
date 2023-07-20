package cn.view.jndc.server_sv.controller;


import cn.view.jndc.server_sv.web_support.utils.ServerUrlConstant;
import jndc.web_support.core.FrontProjectLoader;
import jndc.web_support.model.dto.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
public class DevelopDebugMapping {


    @RequestMapping(ServerUrlConstant.DevelopDebug.reloadFront)
    public HashMap run() {
        FrontProjectLoader.jndcStaticProject.reloadProject();
        HashMap objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("message", "success");
        return objectObjectHashMap;

    }

    @RequestMapping(ServerUrlConstant.DevelopDebug.getDeviceIp)
    public ResponseMessage getDeviceIp(HttpServletRequest request) {
        ResponseMessage responseMessage = new ResponseMessage();
        String remoteAddr = request.getRemoteAddr();
        responseMessage.setMessage("device ip:" + remoteAddr);
        return responseMessage;


    }

}
