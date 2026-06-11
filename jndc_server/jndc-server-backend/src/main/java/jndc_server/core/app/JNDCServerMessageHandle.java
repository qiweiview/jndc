package jndc_server.core.app;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.RegistrationMessage;
import jndc.core.message.TcpServiceDescription;
import jndc.core.message.UserError;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.ObjectSerializableUtils;
import jndc_server.config.JNDCServerConfig;
import jndc_server.core.ChannelHandlerContextHolder;
import jndc_server.core.NDCServerConfigCenter;
import jndc_server.core.ServerServiceDescription;
import jndc_server.databases_object.ServerPortBind;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
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
     * @param serverServiceDescriptions
     */
    public static void serviceBind(List<ServerServiceDescription> serverServiceDescriptions) {
        //配置中心
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //服务转map
        Map<String, ServerServiceDescription> map = serverServiceDescriptions.stream()
                .collect(Collectors.toMap(x -> x.getBindClientId() + x.getServicePort(), x -> x));


        //获取旧的绑定
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        List<ServerPortBind> serverPortBinds = dbWrapper.customQuery("select * from server_port_bind where port_enable=0 and bind_client_id is not null  ");

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

            ServerServiceDescription serverServiceDescription = map.get(s);
            if (serverServiceDescription != null) {
                //todo 重新绑定
                log.debug("rebind:" + map + "----->" + bindClientId);


                //rebind the port service
                boolean success = ndcServerConfigCenter.addTCPRouter(x.getPort(), x.getEnableDateRange(), serverServiceDescription);

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
     * @param ctx 连接上下文，认证失败时用于关闭连接
     * @return
     */
    private NDCMessageProtocol authCheck(NDCMessageProtocol message, String tokenFromRequest, ChannelHandlerContext ctx) {
        JNDCServerConfig jndcServerConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);
        String secrete = jndcServerConfig.getSecrete();

        if (tokenFromRequest == null || !secrete.equals(tokenFromRequest)) {
            //todo auth fail
            message.setType(NDCMessageProtocol.NO_ACCESS);
            UserError userError = new UserError();
            byte[] bytes = ObjectSerializableUtils.object2bytes(userError);
            message.setData(bytes);
            log.error("auth fail with:" + tokenFromRequest);
            ctx.writeAndFlush(message);
            ctx.close();
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
        NDCMessageProtocol checkResult = authCheck(copy, auth, channelHandlerContext);
        if (checkResult != null) {
            return;
        }
        /* ------------------鉴权验证----------------------- */

        //registerServiceProvider
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

        //获取要注销服务列表
        List<TcpServiceDescription> tcpServiceDescriptions = registrationMessage.getTcpServiceDescriptions();

        //转化为复杂服务对象
        List<ServerServiceDescription> serverServiceDescriptions = ServerServiceDescription.ofArray(tcpServiceDescriptions);

        //移除对应服务
        ndcServerConfigCenter.removeServiceByChannelId(registrationMessage.getChannelId(), serverServiceDescriptions);
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
        NDCMessageProtocol checkResult = authCheck(copy, auth, channelHandlerContext);
        if (checkResult != null) {
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
        List<ServerServiceDescription> serverServiceDescriptions = ServerServiceDescription.ofArray(tcpServiceDescriptions);

        //绑定客户端Id
        serverServiceDescriptions.forEach(x -> {
            x.setBindClientId(channelId);
        });

        //服务绑定
        ndcServerConfigCenter.addServiceByChannelId(channelId, serverServiceDescriptions);

        /* ============================= restore the bind relation ============================= */

        //服务绑定
        serviceBind(serverServiceDescriptions);


//        log.info("推送开启刷新");
//        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
//        messageNotificationCenter.dateRefreshMessage("serviceList");//notice the service list refresh
//        messageNotificationCenter.dateRefreshMessage("serverPortList");//notice the server port list refresh
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
        NDCMessageProtocol checkResult = authCheck(copy, auth, channelHandlerContext);
        if (checkResult != null) {
            //todo auth check fail
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
//        MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
//        messageNotificationCenter.dateRefreshMessage("channelList");//notice the channel list refresh
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
            switch (type) {
                case NDCMessageProtocol.CHANNEL_HEART_BEAT:
                    log.debug("get heart beat");
                    OpenChannelMessage registrationMessage = ndcMessageProtocol.getObject(OpenChannelMessage.class);
                    handleHeartBeatFromClient(registrationMessage);
                    break;

                case NDCMessageProtocol.TCP_DATA:
                    NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                    ndcServerConfigCenter.addMessageToReceiveQueue(ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.OPEN_CHANNEL:
                    log.debug("注册隧道...");
                    handleOpenChannel(channelHandlerContext, ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.SERVICE_REGISTER:
                    handleRegisterService(channelHandlerContext, ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.SERVICE_UNREGISTER:
                    handleUnRegisterService(channelHandlerContext, ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.CONNECTION_INTERRUPTED:
                    NDCServerConfigCenter configCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
                    configCenter.connectionInterrupt(ndcMessageProtocol);
                    break;

                case NDCMessageProtocol.NO_ACCESS:
                    log.debug(new String(ndcMessageProtocol.getData()));
                    break;

                case NDCMessageProtocol.USER_ERROR:
                    log.error(new String(ndcMessageProtocol.getData()));
                    break;

                case NDCMessageProtocol.UN_CATCHABLE_ERROR:
                    log.error(new String(ndcMessageProtocol.getData()));
                    break;

                default:
                    // 拒绝未知消息类型
                    log.warn("收到未知消息类型: 0x" + Integer.toHexString(type & 0xFF) + "，拒绝处理");
                    UserError unknownTypeError = new UserError();
                    unknownTypeError.setCode(UserError.SERVER_ERROR);
                    unknownTypeError.setDescription("Unknown message type: 0x" + Integer.toHexString(type & 0xFF));
                    NDCMessageProtocol errResponse = ndcMessageProtocol.copy();
                    errResponse.setType(NDCMessageProtocol.USER_ERROR);
                    errResponse.setData(ObjectSerializableUtils.object2bytes(unknownTypeError));
                    channelHandlerContext.writeAndFlush(errResponse);
                    break;
            }
        } catch (Exception e) {
            log.error("unCatchableError--> " + e);
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
        log.info("客户端断开链接...");
        ndcServerConfigCenter.unRegisterContextHolder(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();


        if (cause.getCause() instanceof SecreteDecodeFailException) {
            //todo 密码错误
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
