package com.view.jndc.core.v2.byte_convert;

import com.view.jndc.core.v2.utils.ByteConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;


@Slf4j
public class ConvertTest {


    @Test
    public void port2ByteArray() {


        int port = 65535;
        byte[] bytes = ByteConversionUtil.portToBytes(port);


        int portBack = ByteConversionUtil.bytesToPort(bytes);
        Assert.assertEquals(port, portBack);

    }

    @Test
    public void ip2ByteArray() {
        String ip = "192.168.1.1";
        byte[] bytes = ByteConversionUtil.ipAddressToBytes(ip);


        String ipBack = ByteConversionUtil.bytesToIPAddress(bytes);
        Assert.assertEquals(ip, ipBack);

    }


}
