package jndc_server.web_support.core;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.ImmediateEventExecutor;
import jndc.utils.JSONUtils;
import jndc.utils.ThreadQueue;

/**
 * broadcast message notification to the web
 */
public class MessageNotificationCenter {
    private static final int TYPE_DATA_REFRESH = 1;

    private static final int TYPE_NOTICE_MESSAGE = 2;

    private ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private ThreadQueue threadQueue;

    public MessageNotificationCenter() {
        threadQueue = new ThreadQueue();
    }

    public void websocketRegister(Channel channel) {
        channelGroup.add(channel);
    }

    public void noticeMessage(String msg) {
        threadQueue.submit(() -> {
            channelGroup.writeAndFlush(new TextWebSocketFrame(new NoticeMessage(TYPE_NOTICE_MESSAGE, msg).toJSON()));
        });

    }

    public void dateRefreshMessage(String msg) {
        threadQueue.submit(() -> {
            channelGroup.writeAndFlush(new TextWebSocketFrame(new NoticeMessage(TYPE_DATA_REFRESH, msg).toJSON()));
        });
    }

    public class NoticeMessage {
        private int type;
        private String data;


        public String toJSON() {
            return JSONUtils.object2JSONString(this);
        }

        public NoticeMessage(int type, String data) {
            this.type = type;
            this.data = data;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }


}
