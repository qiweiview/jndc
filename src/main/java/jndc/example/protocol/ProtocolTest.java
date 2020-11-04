package jndc.example.protocol;

import jndc.core.NDCMessageProtocol;
import jndc.utils.InetUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ProtocolTest {

    private   final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Test
    public void encode() {
        NDCMessageProtocol of = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, 80, 81, 82, NDCMessageProtocol.TCP_DATA);
        of.setData("this is a test data".getBytes());
        byte[] bytes = of.toByteArray();
       logger.debug(of + "\r\n" + Arrays.toString(bytes));
    }

    @Test
    public void decode() {
        NDCMessageProtocol of = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, 80, 81, 82, NDCMessageProtocol.TCP_DATA);
        byte[] data = "1234567890abc`!".getBytes();
        of.setData(data);
        byte[] bytes = of.toByteArray();
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.parseTotal(bytes);
        byte[] data2 = ndcMessageProtocol.getData();
        Assert.assertArrayEquals(data,data2);
    }

    @Test
    public void unPack() {
        NDCMessageProtocol of = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, 80, 81, 82, NDCMessageProtocol.TCP_DATA);
        int i = (35 * 1024 * 1024) + 336;
        of.setData(new byte[i]);
        List<NDCMessageProtocol> list = of.autoUnpack();
        Iterator<NDCMessageProtocol> iterator = list.iterator();
        int i2 = 0;
        while (iterator.hasNext()) {
            NDCMessageProtocol next = iterator.next();
            byte[] bytes = next.getData();
            i2 += bytes.length;
        }
        Assert.assertEquals(i,i2);
    }


}
