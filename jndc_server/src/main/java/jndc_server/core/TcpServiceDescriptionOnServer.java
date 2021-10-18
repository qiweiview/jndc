package jndc_server.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;
import jndc.utils.InetUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 服务描述对象
 */
@Slf4j
@Data
public class TcpServiceDescriptionOnServer extends TcpServiceDescription {




    //客户端唯一编号
    private String bindClientId;

    //服务对应的隧道对象（NAT原因，重连后需要更新）
    private ChannelHandlerContext belongContext;

    //有使用到该服务的监听器，用户后续服务释放时同时释放相应监听器
    //序列化忽略
    @JsonIgnore
    private List<ServerPortProtector> serviceReleaseList = new CopyOnWriteArrayList<>();


    /**
     * 集合转换
     *
     * @param tcpServiceDescriptions
     * @return
     */
    public static List<TcpServiceDescriptionOnServer> ofArray(List<TcpServiceDescription> tcpServiceDescriptions) {
        List<TcpServiceDescriptionOnServer> collect = tcpServiceDescriptions.stream().map(x -> of(x)).collect(Collectors.toList());
        return collect;
    }


    /**
     * 对象转换
     *
     * @param tcpServiceDescription
     * @return
     */
    public static TcpServiceDescriptionOnServer of(TcpServiceDescription tcpServiceDescription) {
        TcpServiceDescriptionOnServer tcpServiceDescriptionOnServer = new TcpServiceDescriptionOnServer();
        tcpServiceDescriptionOnServer.setIp(tcpServiceDescription.getIp());
        tcpServiceDescriptionOnServer.setName(tcpServiceDescription.getName());
        tcpServiceDescriptionOnServer.setPort(tcpServiceDescription.getPort());
        tcpServiceDescriptionOnServer.setDescription(tcpServiceDescription.getDescription());
        tcpServiceDescriptionOnServer.setId(tcpServiceDescription.getId());
        return tcpServiceDescriptionOnServer;
    }


    public void releaseRelatedResources() {
        belongContext = null;
        serviceReleaseList.forEach(x -> {
            //ServerPortProtector
            x.releaseRelatedResources();
        });
    }


    /**
     * @param serverPortProtector
     */
    public void addToServiceReleaseList(ServerPortProtector serverPortProtector) {
        serviceReleaseList.add(serverPortProtector);
    }


    public void sendMessage(NDCMessageProtocol ndcMessageProtocol) {
        //set bind info
        ndcMessageProtocol.setLocalPort(getPort());
        ndcMessageProtocol.setLocalInetAddress(InetUtils.getByStringIpAddress(getIp()));
        belongContext.writeAndFlush(ndcMessageProtocol);
    }


    /**
     * 生成路由信息
     * 客户端唯一编号+服务本地ip+服务本地端口
     *
     * @return
     */
    public String getRouteTo() {
        //context ip + local application ip+ local application port
        return bindClientId + "->" + getIp() + ":" + getPort();
    }


}
