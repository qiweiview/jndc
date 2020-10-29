package jndc.test.protocol;

import jndc.core.NDCMessageProtocol;
import jndc.utils.InetUtils;
import jndc.utils.LogPrint;
import org.junit.Test;

import java.util.Arrays;

public class ProtocolTest {

    @Test
    public void encode(){
        NDCMessageProtocol of = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, 80, 81, 82, NDCMessageProtocol.TCP_DATA);
        of.setData("this is a test data".getBytes());
        byte[] bytes = of.toByteArray();
        LogPrint.log(of+"\r\n"+ Arrays.toString(bytes));
    }

    @Test
    public void decode(){
        NDCMessageProtocol of = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, 80, 81, 82, NDCMessageProtocol.TCP_DATA);
        of.setData("1234567890abc`!".getBytes());
        byte[] bytes = of.toByteArray();
        NDCMessageProtocol ndcMessageProtocol = NDCMessageProtocol.parseTotal(bytes);
        byte[] data = ndcMessageProtocol.getData();
        LogPrint.log(ndcMessageProtocol+"\r\nget data-->"+new String(data));


    }


}
