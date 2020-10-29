package jndc.test;

import jndc.utils.ByteArrayUtils;
import jndc.utils.LogPrint;

import java.util.Arrays;
import java.util.List;

public class SDTest {
    public static void main(String[] args) {
       byte [] bytes={0,1,2,3,4,5,6};

        List<byte[]> list = ByteArrayUtils.bytesUnpack(bytes, 4);
        list.forEach(x->{
            LogPrint.log(Arrays.toString(x));
        });



    }
}
