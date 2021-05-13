package jndc.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

public class ByteBufUtil4V {
    public static  byte[] readWithRelease( ByteBuf byteBuf){
        byte[] bytes = ByteBufUtil.getBytes(byteBuf);
        byteBuf.discardReadBytes();
        byteBuf.release();
        return bytes;
    }


    public static void main(String[] args) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes("".getBytes());
    }
}
