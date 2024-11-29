package com.view.jndc.server.controller.ndc_management;

import com.view.core.client.ndc.NDCClientInfo;
import com.view.core.component.GlobalBeanContext;
import com.view.core.server.ndc.NDCServer;
import com.view.core.server.tcp.TCPServer;
import com.view.jndc.server.model.EncryptedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class NDCManagementController {

   /* private final NDCServer ndcServer;


    @RequestMapping(value = "listNDCClient", method = RequestMethod.GET)
    public EncryptedResponse listNDCClient() {
        List<String> ndcClientInfoList = new ArrayList<>();
        Map<String, NDCClientInfo> ndcClientSessionMap = ndcServer.getNdcClientSessionMap();
        ndcClientSessionMap.forEach((k, v) -> {
            ndcClientInfoList.add(v.formatDescription());
        });
        return EncryptedResponse.success(ndcClientInfoList);
    }

    @RequestMapping(value = "listRunningAPP", method = RequestMethod.GET)
    public EncryptedResponse listRunningAPP() {
        List<String> ndcClientInfoList = new ArrayList<>();
        Map<String, TCPServer> tcpServerMap = GlobalBeanContext.APP_CENTER.getTcpServerMap();
        tcpServerMap.forEach((k, v) -> {
            ndcClientInfoList.add(v.getDescription());
        });
        return EncryptedResponse.success(ndcClientInfoList);
    }*/
}
