package jndc_server.core;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jndc.core.NDCMessageProtocol;
import jndc.core.TcpServiceDescription;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.UserError;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.ObjectSerializableUtils;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.core.MessageNotificationCenter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 通信消息处理
 */
@Slf4j
public class JNDCServerMessageHandle extends SimpleChannelInboundHandler<NDCMessageProtocol> {
    public static final String NAME = "NDC_SERVER_HANDLE";


    /**
     * 服务绑定
     *
     * @param tcpServiceDescriptionOnServers
     */
    public static void serviceBind(List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers) {
        //配置中心
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //服务转map
        Map<String, TcpServiceDescriptionOnServer> map = tcpServiceDescriptionOnServers.stream()
                .collect(Collectors.toMap(x -> x.getBindClientId() + x.getPort(), x -> x));


        //获取旧的绑定
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        List<ServerPortBind> serverPortBinds = dbWrapper.customQuery("select * from server_port_bind where portEnable=0 and bindClientId is not null  ");

        //获取库内规则进行匹配
        serverPortBinds.forEach(x -> {
            String bindClientId = x.getBindClientId();
            String routeTo = x.getRouteTo();
            if (routeTo == null) {
                return;
            }

            int index = routeTo.lastIndexOf(":") + 1;
            if (index == 0) {
                //todo  not right route to
                return;
            }

            String s = bindClientId + routeTo.substring(index);

            log.debug("db old key" + s);

            TcpServiceDescriptionOnServer tcpServiceDescriptionOnServer = map.get(s);
            if (tcpServiceDescriptionOnServer != null) {
                //todo 重新绑定
                log.debug("rebind:" + map + "----->" + bindClientId);


                //rebind the port service
                boolean success = ndcServerConfigCenter.addTCPRouter(x.getPort(), x.getEnableDateRange(), tcpServiceDescriptionOnServer);

                if (success) {
                    x.bindEnable();
                    log.info("rebind the service:" + routeTo + " success");
                } else {
                    x.bindDisable();
                    log.error("rebind the service:" + routeTo + " fail");
                }

                //修改库内信息
                dbWrapper.updateByPrimaryKey(x);

            }
        });
    }


    /**
     * 鉴权
     *
     * @param message
     * @param tokenFromRequest
     * @return
     */
    private NDCMessageProtocol authCheck(NDCMessageProtocol message, String tokenFromRequest) {
        JNDCServerConfig jndcServerConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);
        String secrete = jndcServerConfig.getSecrete();

        if (tokenFromRequest == null || !secrete.equals(tokenFromRequest)) {
            //todo auth fail
            message.setType(NDCMessageProtocol.NO_ACCESS);
            UserError userError = new UserError();
            byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
            message.setData(bytes);
            log.error("auth fail with:" + tokenFromRequest);
            return message;
        }
        return null;
    }


    /**
     * 取消注册消息处理
     *
     * @param channelHandlerContext
     * @param ndcMessageProtocol
     */
    private void handleUnRegisterService(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) {
        //copy message
        NDCMessageProtocol copy = ndcMessageProtocol.copy();

        /* ------------------鉴权验证----------------------- */
        RegistrationMessage registrationMessage = ndcMessageProtocol.getObject(RegistrationMessage.class);
        String auth = registrationMessage.getAuth();
        NDCMessageProtocol checkResult = authCheck(copy, auth);
        if (checkResult != null) {
            channelHandlerContext.writeAndFlush(copy);
            return;
        }
        /* ------------------鉴权验证----------------------- */

        //registerServiceProvider
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //获取要注销服务列表
        List<TcpServiceDescription> tcpServiceDescriptions = registrationMessage.getTcpServiceDescriptions();

        //转化为复杂服务对象
        List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers = TcpServiceDescriptionOnServer.ofArray(tcpServiceDescriptions);

        //移除对应服务
        ndcServerConfigCenter.removeServiceByChannelId(registrationMessage.getChannelId(), tcpServiceDescriptionOnServers);
    }

    /**
     * 处理服务注册消息
     *
     * @param channelHandlerContext
     * @param ndcMessageProtocol
     */
    private void handleRegisterService(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) {
        //copy message
        NDCMessageProtocol copy = ndcMessageProtocol.copy();


        //安全验证
        /*-------------- 安全验证 -------------- */
        RegistrationMessage registrationMessage = ndcMessageProtocol.getObject(RegistrationMessage.class);
        String auth = registrationMessage.getAuth();
        NDCMessageProtocol checkResult = authCheck(copy, auth);
        if (checkResult != null) {
            channelHandlerContext.writeAndFlush(copy);
            return;
        }
        /*-------------- 安全验证 -------------- */


        //获取注册中心
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);


        //服务端唯一Id
        String channelId = registrationMessage.getChannelId();


        //提取注册服务集合
        List<TcpServiceDescription> tcpServiceDescriptions = registrationMessage.getTcpServiceDescriptions();

        //转化为服务端服务对象（复杂）集合
        List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers = TcpServiceDescriptionOnServer.ofArray(tcpServiceDescriptions);

        //绑定客户端Id
        tcpServiceDescriptionOnServers.forEach(x -> {
            x.setBindClientId(channelId);
        });

        //服务绑定
        ndcServerConfigCenter.addServiceByChannelId(channelId, tcpServiceDescriptionOnServers);

        /* ============================= restore the bind relation ============================= */

        //服务绑定
        serviceBind(tcpServiceDescriptionOnServers);


        log.info("推送开启刷新");
        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
        messageNotificationCenter.dateRefreshMessage("serverPortList");//notice the server port list refresh
    }

    /**
     * 通道打开消息处理
     *
     * @param channelHandlerContext
     * @param ndcMessageProtocol
     */
    private void handleOpenChannel(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) {
        //复制消息
        NDCMessageProtocol copy = ndcMessageProtocol.copy();


        /* -------------------- 鉴权 -------------------- */
        OpenChannelMessage openChannelMessage = ndcMessageProtocol.getObject(OpenChannelMessage.class);
        String auth = openChannelMessage.getAuth();
        NDCMessageProtocol checkResult = authCheck(copy, auth);
        if (checkResult != null) {
            //todo auth check fail
            channelHandlerContext.writeAndFlush(copy);
            return;
        }
        /* -------------------- 鉴权 -------------------- */


        //获取注册中心
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //构建上下文描述对象
        ChannelHandlerContextHolder channelHandlerContextHolder = new ChannelHandlerContextHolder(openChannelMessage.getChannelId());

        //设置上下文,解析上下文基础参数
        channelHandlerContextHolder.setChannelHandlerContextWithParse(channelHandlerContext);

        //向注册中心注册上下文
        ndcServerConfigCenter.registerServiceProvider(channelHandlerContextHolder);


        //发送响应
        copy.setType(NDCMessageProtocol.OPEN_CHANNEL);
        OpenChannelMessage response = new OpenChannelMessage();
        response.setChannelId(channelHandlerContextHolder.getClientId());
        byte[] bytes = ObjectSerializableUtils.object2bytes(response);
        copy.setData(bytes);
        channelHandlerContext.writeAndFlush(copy);

        //通知前端刷新列表
        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
        messageNotificationCenter.dateRefreshMessage("channelList");//notice the channel list refresh
    }

    /**
     * 处理心跳消息
     *
     * @param registrationMessage
     */
    private void handleHeartBeatFromClient(OpenChannelMessage registrationMessage) {
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        //刷新心跳时间
        ndcServerConfigCenter.refreshHeartBeatTimeStamp(registrationMessage.getChannelId());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol) throws Exception {
        byte type = ndcMessageProtocol.getType();


        try {
            /*==================================== CHANNEL_HEART_BEAT ====================================*/
            if (type == NDCMessageProtocol.CHANNEL_HEART_BEAT) {
                //todo CHANNEL_HEART_BEAT
                //just accept
                log.debug("get heart beat");

                OpenChannelMessage registrationMessage = ndcMessageProtocol.getObject(OpenChannelMessage.class);
                handleHeartBeatFromClient(registrationMessage);

            }

            /*==================================== TCP_DATA ====================================*/
            if (type == NDCMessageProtocol.TCP_DATA) {
                //todo TCP_DATA
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.addMessageToReceiveQueue(ndcMessageProtocol);
            }

            /*==================================== OPEN_CHANNEL ====================================*/
            if (type == NDCMessageProtocol.OPEN_CHANNEL) {
                //todo OPEN_CHANNEL 打开通道
                handleOpenChannel(channelHandlerContext, ndcMessageProtocol);
            }

            /*==================================== SERVICE_REGISTER ====================================*/
            if (type == NDCMessageProtocol.SERVICE_REGISTER) {
                //todo SERVICE_REGISTER 处理服务注册消息
                handleRegisterService(channelHandlerContext, ndcMessageProtocol);
            }


            /*==================================== SERVICE_UNREGISTER ====================================*/
            if (type == NDCMessageProtocol.SERVICE_UNREGISTER) {
                //todo SERVICE_UNREGISTER 服务取消注册消息
                handleUnRegisterService(channelHandlerContext, ndcMessageProtocol);

                //消息通知中心
                MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
                log.debug("推送关闭刷新");
                messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
            }

            /*==================================== CONNECTION_INTERRUPTED ====================================*/
            if (type == NDCMessageProtocol.CONNECTION_INTERRUPTED) {
                //todo CONNECTION_INTERRUPTED
                NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                ndcServerConfigCenter.connectionInterrupt(ndcMessageProtocol);


            }

            /*==================================== NO_ACCESS ====================================*/
            if (type == NDCMessageProtocol.NO_ACCESS) {
                //todo NO_ACCESS
                log.debug(new String(ndcMessageProtocol.getData()));
            }

            /*==================================== USER_ERROR ====================================*/
            if (type == NDCMessageProtocol.USER_ERROR) {
                //todo USER_ERROR
                log.error(new String(ndcMessageProtocol.getData()));

            }

            /*==================================== UN_CATCHABLE_ERROR ====================================*/
            if (type == NDCMessageProtocol.UN_CATCHABLE_ERROR) {
                //todo UN_CATCHABLE_ERROR
                log.error(new String(ndcMessageProtocol.getData()));
            }

        } catch (Exception e) {
            log.error("unCatchableError--> " + e.getMessage() + "/" + Arrays.toString(e.getStackTrace()));
            ndcMessageProtocol.setType(NDCMessageProtocol.USER_ERROR);
            UserError userError = new UserError();
            userError.setCode(UserError.SERVER_ERROR);
            byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
            ndcMessageProtocol.setData(bytes);
            channelHandlerContext.writeAndFlush(ndcMessageProtocol);
        }


    }


    /**
     * 连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        //配置中心
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //取消服务上下文描述
        ndcServerConfigCenter.unRegisterContextHolder(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();


        if (cause.getCause() instanceof SecreteDecodeFailException) {
            NDCMessageProtocol of = NDCMessageProtocol.of(localAddress.getAddress(), remoteAddress.getAddress(), 0, localAddress.getPort(), remoteAddress.getPort(), NDCMessageProtocol.NO_ACCESS);
            ctx.writeAndFlush(of).addListeners(ChannelFutureListener.CLOSE);
            log.error("The \"" + remoteAddress + "\" is broken due to incorrect credentials");
            return;
        }


        //for the client local is remote
        NDCMessageProtocol of = NDCMessageProtocol.of(localAddress.getAddress(), remoteAddress.getAddress(), 0, localAddress.getPort(), remoteAddress.getPort(), NDCMessageProtocol.UN_CATCHABLE_ERROR);
        of.setData(cause.toString().getBytes());
        ctx.writeAndFlush(of).addListeners(ChannelFutureListener.CLOSE);

        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }
        log.error("unCatchable server error：" + cause);

    }


}
