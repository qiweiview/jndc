package jndc_server.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.channel.ChannelHandlerContext;
import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;
import jndc.utils.InetUtils;
import jndc.utils.NettyContextUtils;
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

    private volatile boolean released = false;

    //来源客户端唯一编号
    private String bindClientId;

    //服务对应的隧道对象（NAT原因，重连后需要更新）
    private ChannelHandlerContext belongContext;

    //反向引用
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

    /**
     * 释放服务
     */
    public void releaseRelatedResourcesWithCheck(String id) {
        if (released) {
            //todo 已释放
            return;
        }


        String check = NettyContextUtils.getFingerprintFromContext(belongContext);

        if (id.equals(check)) {
            //todo 命中则释放服务,可能出现情况：服务持有的隧道不是当前断开的隧道

            log.info("服务内隧道命中...");

            belongContext = null;


            serviceReleaseList.forEach(x -> {
                //todo 释放绑定该服务的端口监听器
                x.releaseRelatedResources();
            });

            released = true;
        }


    }

    /**
     * 释放服务
     */
    public void releaseRelatedResources() {
        if (released) {
            //todo 已释放
            return;
        }

        //释放引用
        belongContext = null;


        serviceReleaseList.forEach(x -> {
            //todo 释放绑定该服务的端口监听器
            x.releaseRelatedResources();
        });

        released = true;
    }


    /**
     * 将服务放入服务释放集合内
     *
     * @param serverPortProtector
     */
    public void addToServiceReleaseList(ServerPortProtector serverPortProtector) {
        serviceReleaseList.add(serverPortProtector);
    }


    public void sendMessage(NDCMessageProtocol ndcMessageProtocol) {
        //set bind info
        ndcMessageProtocol.setLocalPort(getPort());
        ndcMessageProtocol.setLocalInetAddress(InetUtils.getByStringIpAddress(getIp()));

        //向隧道上下文发送消息
        belongContext.writeAndFlush(ndcMessageProtocol);
    }


    /**
     * 生成路由唯一编号
     * 客户端id + 来源服务ip + 来源服务端口
     *
     * @return
     */
    public String getRouteTo() {
        //客户端id + 来源服务ip + 来源服务端口
        return bindClientId + "->" + getIp() + ":" + getPort();
    }


    /**
     * 刷新context
     *
     * @param channelHandlerContext
     */
    public void refreshContext(ChannelHandlerContext channelHandlerContext) {
        String newest = NettyContextUtils.getFingerprintFromContext(channelHandlerContext);

        String current = NettyContextUtils.getFingerprintFromContext(belongContext);

        if (!current.equals(newest)) {
            log.info("context 替换");

            //关闭当前context
            belongContext.close();

            //赋值新context
            belongContext = channelHandlerContext;

        }

    }
}
