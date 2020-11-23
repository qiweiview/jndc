package jndc.core;

import io.netty.channel.ChannelHandlerContext;
import jndc.server.ServerPortProtector;
import jndc.utils.InetUtils;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * the description of service supported by client
 */
public class TcpServiceDescription implements Serializable {


    private static final long serialVersionUID = -6570101717300836163L;

    private String id;

    private int port;

    private String ip;//the service ip in the client net before NAT

    private String name;

    private String description;

    private String belongContextIp;//the channel ip

    private ChannelHandlerContext belongContext;

    private List<ServerPortProtector> serviceReleaseList=new CopyOnWriteArrayList<>();


    public void  addToServiceReleaseList(ServerPortProtector serverPortProtector){
        serviceReleaseList.add(serverPortProtector);
    }

    public void sendMessage(NDCMessageProtocol ndcMessageProtocol) {
        //set bind info
        ndcMessageProtocol.setLocalPort(getPort());
        ndcMessageProtocol.setLocalInetAddress(InetUtils.getByStringIpAddress(getIp()));
        belongContext.writeAndFlush(ndcMessageProtocol);
    }

    public void releaseRelatedResources() {
        belongContext=null;
        serviceReleaseList.forEach(x->{
            //ServerPortProtector
            x.releaseRelatedResources();
        });
    }


    public String getRouteTo(){
        return belongContextIp+"->"+ip+":"+port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelongContextIp() {
        return belongContextIp;
    }

    public void setBelongContextIp(String belongContextIp) {
        this.belongContextIp = belongContextIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public ChannelHandlerContext getBelongContext() {
        return belongContext;
    }

    public void setBelongContext(ChannelHandlerContext belongContext) {
        this.belongContext = belongContext;
    }

    @Override
    public String toString() {
        return "TcpServiceDescription{" +
                "port=" + port +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}
