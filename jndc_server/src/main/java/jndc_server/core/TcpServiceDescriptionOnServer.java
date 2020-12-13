package jndc_server.core;

import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;
import jndc.utils.InetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpServiceDescriptionOnServer extends TcpServiceDescription {


    private String belongContextIp;//the channel ip

    private ChannelHandlerContext belongContext;

    private List<ServerPortProtector> serviceReleaseList=new CopyOnWriteArrayList<>();

    public static List<TcpServiceDescriptionOnServer> ofArray(List<TcpServiceDescription> tcpServiceDescriptions) {
        List<TcpServiceDescriptionOnServer> list=new ArrayList<>();
        tcpServiceDescriptions.forEach(x->{
            list.add(of(x));
        });
        return list;
    }

    public static TcpServiceDescriptionOnServer of(TcpServiceDescription tcpServiceDescription){
        TcpServiceDescriptionOnServer tcpServiceDescriptionOnServer = new TcpServiceDescriptionOnServer();
        tcpServiceDescriptionOnServer.setIp(tcpServiceDescription.getIp());
        tcpServiceDescriptionOnServer.setName(tcpServiceDescription.getName());
        tcpServiceDescriptionOnServer.setPort(tcpServiceDescription.getPort());
        tcpServiceDescriptionOnServer.setDescription(tcpServiceDescription.getDescription());
        tcpServiceDescriptionOnServer.setId(tcpServiceDescription.getId());
        return tcpServiceDescriptionOnServer;
    }


    public void releaseRelatedResources() {
        belongContext=null;
        serviceReleaseList.forEach(x->{
            //ServerPortProtector
            x.releaseRelatedResources();
        });
    }


    public void  addToServiceReleaseList(ServerPortProtector serverPortProtector){
        serviceReleaseList.add(serverPortProtector);
    }



    public void sendMessage(NDCMessageProtocol ndcMessageProtocol) {
        //set bind info
        ndcMessageProtocol.setLocalPort(getPort());
        ndcMessageProtocol.setLocalInetAddress(InetUtils.getByStringIpAddress(getIp()));
        belongContext.writeAndFlush(ndcMessageProtocol);
    }




    public String getRouteTo(){
        //context ip + local application ip+ local application port
        return belongContextIp+"->"+getIp()+":"+getPort();
    }



    public String getBelongContextIp() {
        return belongContextIp;
    }

    public void setBelongContextIp(String belongContextIp) {
        this.belongContextIp = belongContextIp;
    }



    public ChannelHandlerContext getBelongContext() {
        return belongContext;
    }

    public void setBelongContext(ChannelHandlerContext belongContext) {
        this.belongContext = belongContext;
    }


}
