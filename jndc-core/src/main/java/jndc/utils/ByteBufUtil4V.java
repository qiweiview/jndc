package jndc.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public class ByteBufUtil4V {
    public static  byte[] readWithRelease( ByteBuf byteBuf){
        byte[] bytes = ByteBufUtil.getBytes(byteBuf);
        byteBuf.discardReadBytes();
        byteBuf.release();
        return bytes;
    }


}
