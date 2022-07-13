package jndc_client.web_support.mapping;


import jndc.utils.JSONUtils;
import jndc.web_support.core.JNDCHttpRequest;
import jndc.web_support.core.WebMapping;
import jndc.web_support.model.dto.LoginUser;
import jndc.web_support.model.dto.ResponseMessage;
import jndc.web_support.utils.AuthUtils;
import jndc_client.web_support.utils.ClientUrlConstant;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * singleton， thread unsafe
 */
@Slf4j
public class ManageMapping {

    /**
     * 登录
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = ClientUrlConstant.Management.login)
    public ResponseMessage login(JNDCHttpRequest jndcHttpRequest) {


        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        LoginUser loginUser = JSONUtils.str2Object(s, LoginUser.class);
        if (AuthUtils.doLogin(loginUser)) {
            InetAddress remoteAddress = jndcHttpRequest.getRemoteAddress();
            byte[] address = remoteAddress.getAddress();

            //时间戳
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(System.currentTimeMillis() + 60 * 60 * 1000);
            byte[] array = buffer.array();

            //mix data
            byte[] newByte = new byte[address.length + 8];
            for (int i = 0; i < newByte.length; ++i) {
                newByte[i] = i < array.length ? array[i] : address[i - array.length];
            }

            //编码
            String s1 = AuthUtils.webAuthTokenEncode(newByte);
            return ResponseMessage.success(s1);

        } else {
            return ResponseMessage.fail("密码错误");
        }
    }




}
