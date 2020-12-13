package jndc_server.web_support.model.view_object;

import jndc_server.core.ChannelHandlerContextHolder;

public class ChannelContextVO {

    private String id;

    private int supportServiceNum;

    private int channelClientPort;

    private String channelClientIp;




    public static ChannelContextVO of(ChannelHandlerContextHolder channelHandlerContextHolder) {
        ChannelContextVO facePortVO = new ChannelContextVO();

        facePortVO.setId(channelHandlerContextHolder.getId());
        facePortVO.setSupportServiceNum(channelHandlerContextHolder.serviceNum());
        facePortVO.setChannelClientIp(channelHandlerContextHolder.getContextIp());
        facePortVO.setChannelClientPort(channelHandlerContextHolder.getContextPort());

        return facePortVO;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSupportServiceNum() {
        return supportServiceNum;
    }

    public void setSupportServiceNum(int supportServiceNum) {
        this.supportServiceNum = supportServiceNum;
    }

    public int getChannelClientPort() {
        return channelClientPort;
    }

    public void setChannelClientPort(int channelClientPort) {
        this.channelClientPort = channelClientPort;
    }

    public String getChannelClientIp() {
        return channelClientIp;
    }

    public void setChannelClientIp(String channelClientIp) {
        this.channelClientIp = channelClientIp;
    }
}
