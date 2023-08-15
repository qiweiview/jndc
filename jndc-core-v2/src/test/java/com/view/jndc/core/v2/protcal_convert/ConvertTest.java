package com.view.jndc.core.v2.protcal_convert;

import com.view.jndc.core.v2.model.jndc.JNDCData;
import com.view.jndc.core.v2.model.protocol_message.JNDCDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ConvertTest {

    @Test
    public void test() {
        JNDCData jndcData = new JNDCData();
        jndcData.setSourceAddress("10.25.73.202");
        jndcData.setDestAddress("192.168.1.1");
        jndcData.setSourcePort(53002);
        jndcData.setProxyPort(777);
        jndcData.setDestPort(8080);

        JNDCDataMessage jndcDataMessage = jndcData.authMessage();

        byte[] bytes = jndcDataMessage.toTransferFormat();

        JNDCDataMessage newMessage = JNDCDataMessage.toEncodedFormat(bytes);


        JNDCData parse = JNDCData.parse(newMessage);

        log.info(parse.toString());


    }
}
