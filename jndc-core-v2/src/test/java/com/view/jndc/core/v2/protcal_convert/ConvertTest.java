package com.view.jndc.core.v2.protcal_convert;

import com.view.jndc.core.v2.enum_value.JNDCMessageType;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import com.view.jndc.core.v2.model.protocol_message.JNDCEncoded;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class ConvertTest {

    /**
     * 报文转换单元测试
     */
    @Test
    public void test() {
        String sourceIp = "10.25.73.202";
        JNDCData jndcData = new JNDCData();
        jndcData.setSourceAddress(sourceIp);
        jndcData.setDestAddress("192.168.1.1");
        jndcData.setSourcePort(53002);
        jndcData.setProxyPort(777);
        jndcData.setDestPort(8080);
        jndcData.setType(JNDCMessageType.CHANNEL_0X10.value);

        JNDCEncoded jndcEncoded = jndcData.toEncoded();

        //转为传输格式
        byte[] bytes = jndcEncoded.toTransferFormat();

        //转为报文格式
        JNDCEncoded encodedFormat = JNDCEncoded.toEncodedFormat(bytes);

        //转为编码对象
        JNDCData data = JNDCData.parse(encodedFormat);


        //断言
        Assert.assertEquals(sourceIp, data.getSourceAddress());


    }
}
