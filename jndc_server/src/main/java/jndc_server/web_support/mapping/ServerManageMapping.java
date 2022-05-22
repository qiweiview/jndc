package jndc_server.web_support.mapping;


import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.PageResult;
import jndc.core.message.TcpServiceDescription;
import jndc.utils.JSONUtils;
import jndc.utils.LogPrint;
import jndc.utils.UUIDSimple;
import jndc_server.core.AsynchronousEventCenter;
import jndc_server.core.ChannelHandlerContextHolder;
import jndc_server.core.NDCServerConfigCenter;
import jndc_server.core.ServerServiceDescription;
import jndc_server.core.filter.IpChecker;
import jndc_server.core.port_app.ServerPortProtector;
import jndc_server.databases_object.ChannelContextCloseRecord;
import jndc_server.databases_object.IpFilterRecord;
import jndc_server.databases_object.IpFilterRule4V;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.core.JNDCHttpRequest;
import jndc_server.web_support.core.MessageNotificationCenter;
import jndc_server.web_support.core.WebMapping;
import jndc_server.web_support.model.data_object.ManagementLoginUser;
import jndc_server.web_support.model.data_transfer_object.*;
import jndc_server.web_support.model.view_object.ChannelContextVO;
import jndc_server.web_support.model.view_object.DeviceInfo;
import jndc_server.web_support.model.view_object.IpRecordVO;
import jndc_server.web_support.model.view_object.PageListVO;
import jndc_server.web_support.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * singleton， thread unsafe
 */
@Slf4j
public class ServerManageMapping {

    /**
     * do login
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.login)
    public HashMap login(JNDCHttpRequest jndcHttpRequest) {
        HashMap objectObjectHashMap = new HashMap<>();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ManagementLoginUser managementLoginUser = JSONUtils.str2Object(s, ManagementLoginUser.class);
        if (AuthUtils.doLogin(managementLoginUser)) {
            InetAddress remoteAddress = jndcHttpRequest.getRemoteAddress();
            byte[] address = remoteAddress.getAddress();

            //timestamp to byte array
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(System.currentTimeMillis() + 60 * 60 * 1000);
            byte[] array = buffer.array();

            //mix data
            byte[] newByte = new byte[address.length + 8];
            for (int i = 0; i < newByte.length; ++i) {
                newByte[i] = i < array.length ? array[i] : address[i - array.length];
            }

            //token encode
            String s1 = AuthUtils.webAuthTokenEncode(newByte);
            objectObjectHashMap.put("token", s1);


        } else {
            objectObjectHashMap.put("token", "403");
        }


        return objectObjectHashMap;

    }

    /**
     * get the active channel list
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.getServerChannelTable)
    public List<ChannelContextVO> getServerChannelTable(JNDCHttpRequest jndcHttpRequest) {

        List<ChannelContextVO> list = new ArrayList<>();
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();
        channelHandlerContextHolders.forEach(x -> {
            list.add(ChannelContextVO.of(x));
        });


        return list;

    }

    /**
     * getChannelRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.getChannelRecord)
    public PageListVO<ChannelContextCloseRecord> getChannelRecord(JNDCHttpRequest jndcHttpRequest) {

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        PageDTO pageDTO = JSONUtils.str2Object(s, PageDTO.class);

        DBWrapper<ChannelContextCloseRecord> dbWrapper = DBWrapper.getDBWrapper(ChannelContextCloseRecord.class);
        PageResult<ChannelContextCloseRecord> channelContextCloseRecordPageResult = dbWrapper.customQueryByPage("select * from channel_context_record order by time_stamp desc", pageDTO.getPage(), pageDTO.getRows());

        //create vo
        PageListVO<ChannelContextCloseRecord> channelContextCloseRecordPageListVO = new PageListVO<>();
        channelContextCloseRecordPageListVO.setPage(pageDTO.getPage());
        channelContextCloseRecordPageListVO.setRows(pageDTO.getRows());
        channelContextCloseRecordPageListVO.setData(channelContextCloseRecordPageResult.getData());
        channelContextCloseRecordPageListVO.setTotal(channelContextCloseRecordPageResult.getTotal());

        return channelContextCloseRecordPageListVO;
    }


    /**
     * clearChannelRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.clearChannelRecord)
    public ResponseMessage clearChannelRecord(JNDCHttpRequest jndcHttpRequest) {

        DBWrapper<ChannelContextCloseRecord> dbWrapper = DBWrapper.getDBWrapper(ChannelContextCloseRecord.class);
        dbWrapper.customExecute("delete from channel_context_record;");

        return new ResponseMessage();
    }


    /**
     * sendHeartBeat
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.sendHeartBeat)
    public ResponseMessage sendHeartBeat(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ChannelContextVO channelContextVO = JSONUtils.str2Object(s, ChannelContextVO.class);
        String id = channelContextVO.getId();

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        bean.sendHeartBeat(id);

        return new ResponseMessage();

    }


    /**
     * close channel by id
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.closeChannelByServer)
    public ResponseMessage closeChannelByServer(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ChannelContextVO channelContextVO = JSONUtils.str2Object(s, ChannelContextVO.class);
        String id = channelContextVO.getId();

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        bean.unRegisterContextHolder(id);

        return new ResponseMessage();

    }


    /**
     * getServiceList
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.getServiceList)
    public List<TcpServiceDescription> getServiceList(JNDCHttpRequest jndcHttpRequest) {


        List<TcpServiceDescription> tcpServiceDescriptions = new ArrayList<>();

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();

        channelHandlerContextHolders.forEach(x -> {
            tcpServiceDescriptions.addAll(x.getTcpServiceDescriptions());
        });
        return tcpServiceDescriptions;

    }


    /**
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.getServerPortList)
    public List<ServerPortBind> getServerPortList(JNDCHttpRequest jndcHttpRequest) {


        ServerPortBind serverPortBind = jndcHttpRequest.getObject(ServerPortBind.class);
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        List<ServerPortBind> serverPortBinds;
        if (0 == serverPortBind.getPort()) {
            serverPortBinds = dbWrapper.customQuery("select * from server_port_bind order by port_enable desc ");
        } else {
            serverPortBinds = dbWrapper.customQuery("select * from server_port_bind where port =? order by port_enable desc ", serverPortBind.getPort());
        }

        return serverPortBinds;

    }


    /**
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.createPortMonitoring)
    public ResponseMessage createPortMonitoring(JNDCHttpRequest jndcHttpRequest) {


        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServerPortBind channelContextVO = JSONUtils.str2Object(s, ServerPortBind.class);


        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);

        List<ServerPortBind> serverPortBinds = dbWrapper.customQuery("select * from server_port_bind where port=?", channelContextVO.getPort());
        ResponseMessage responseMessage = new ResponseMessage();
        if (serverPortBinds.size() > 0) {
            responseMessage.error();
            responseMessage.setMessage("端口 " + channelContextVO.getPort() + " 已被占用");
            return responseMessage;
        }


        //do create
        channelContextVO.bindDisable();
        channelContextVO.setId(UUIDSimple.id());
        dbWrapper.insert(channelContextVO);
        return responseMessage;

    }


    /**
     * do service bind
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.doServiceBind)
    public ResponseMessage doServiceBind(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServiceBindDTO channelContextVO = JSONUtils.str2Object(s, ServiceBindDTO.class);

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        ServerPortBind serverPortBind = dbWrapper.customQuerySingle("select * from server_port_bind where id=?", channelContextVO.getServerPortId());
        if (serverPortBind == null) {
            responseMessage.error();
            responseMessage.setMessage("端口监听不存在");
            return responseMessage;
        }

        //异步执行中心
        AsynchronousEventCenter asynchronousEventCenter = UniqueBeanManage.getBean(AsynchronousEventCenter.class);

        //asynchronous
        asynchronousEventCenter.systemRunningJob(() -> {
            //todo 异步执行

            AtomicBoolean atomicBoolean = new AtomicBoolean(true);

            //配置中心
            NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);

            //获取上下文对象集合
            List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();

            //遍历上下文集合匹配所有服务
            channelHandlerContextHolders.forEach(x -> {
                if (atomicBoolean.get()) {//find the only one service ,if service has been found,ignore other data

                    List<ServerServiceDescription> tcpServiceDescriptions = x.getTcpServiceDescriptions();

                    tcpServiceDescriptions.forEach(service -> {
                        //todo 这里的y是 上下文中注册的服务
                        if (service.getId().equals(channelContextVO.getServiceId())) {//find the service from contextHolder


                            //设置服务路由路径
                            serverPortBind.setRouteTo(service.getRouteTo());

                            //set belong id
                            serverPortBind.setBindClientId(x.getClientId());

                            //do open-port operation
                            boolean success = bean.addTCPRouter(serverPortBind.getPort(), serverPortBind.getEnableDateRange(), service);

                            if (success) {
                                //update databases state

                                //set true
                                serverPortBind.bindEnable();
                            } else {
                                //set false
                                serverPortBind.bindDisable();
                                serverPortBind.setRouteTo(null);
                                serverPortBind.setBindClientId(null);
                            }
                            dbWrapper.updateByPrimaryKey(serverPortBind);


                            //notice refresh data
                            MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
                            if (success) {
//                                messageNotificationCenter.dateRefreshMessage("serverPortList");
                            } else {
                                messageNotificationCenter.noticeMessage(serverPortBind.getPort() + "端口服务关联失败");
                            }


                            //bind just once
                            atomicBoolean.set(false);
                        }
                    });
                }
            });


        });


        serverPortBind.bindPreparing();
        dbWrapper.updateByPrimaryKey(serverPortBind);


        return responseMessage;

    }


    /**
     * do Date Range Edit
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.doDateRangeEdit)
    public ResponseMessage doDateRangeEdit(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServiceBindDTO channelContextVO = JSONUtils.str2Object(s, ServiceBindDTO.class);

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        ServerPortBind serverPortBind = dbWrapper.customQuerySingle("select * from server_port_bind where id=?", channelContextVO.getServerPortId());
        if (serverPortBind == null) {
            responseMessage.error();
            responseMessage.setMessage("端口监听不存在");
            return responseMessage;
        }

        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        Map<Integer, AsynchronousEventCenter.ServerPortBindContext> tcpRouter = bean.getTcpRouter();
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext = tcpRouter.get(serverPortBind.getPort());

        if (serverPortBindContext != null) {
            ServerPortProtector serverPortProtector = serverPortBindContext.getServerPortProtector();
            serverPortProtector.parseEnableDateRange(channelContextVO.getEnableDateRange());

            //reset all connection
            serverPortProtector.resetAllConnection();

        } else {
            log.debug("can not found the service on server port " + serverPortBind.getPort());
        }

        //do db info update
        serverPortBind.setEnableDateRange(channelContextVO.getEnableDateRange());
        serverPortBind.setName(channelContextVO.getRemark());
        dbWrapper.updateByPrimaryKey(serverPortBind);

        return responseMessage;

    }


    /**
     * deleteServiceBindRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.deleteServiceBindRecord)
    public ResponseMessage deleteServiceBindRecord(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServiceBindDTO channelContextVO = JSONUtils.str2Object(s, ServiceBindDTO.class);

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("delete from server_port_bind where id=?", channelContextVO.getServerPortId());


        return responseMessage;

    }


    /**
     * resetBindRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.resetBindRecord)
    public ResponseMessage resetBindRecord(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServiceBindDTO channelContextVO = JSONUtils.str2Object(s, ServiceBindDTO.class);

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("update  server_port_bind set route_to=null where id=?", channelContextVO.getServerPortId());


        return responseMessage;

    }


    /**
     * 停止服务绑定
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.stopServiceBind)
    public ResponseMessage stopServiceBind(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServiceBindDTO channelContextVO = JSONUtils.str2Object(s, ServiceBindDTO.class);

        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        ServerPortBind serverPortBind = dbWrapper.customQuerySingle("select * from server_port_bind where id=?", channelContextVO.getServerPortId());
        if (serverPortBind == null) {
            responseMessage.error();
            responseMessage.setMessage("端口监听不存在");
            return responseMessage;
        }

        int port = serverPortBind.getPort();
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        Map<Integer, AsynchronousEventCenter.ServerPortBindContext> tcpRouter = bean.getTcpRouter();
        AsynchronousEventCenter.ServerPortBindContext serverPortBindContext = tcpRouter.get(port);


        if (serverPortBindContext != null) {
            serverPortBindContext.releaseRelatedResources();
            tcpRouter.remove(port);
        }

        serverPortBind.bindDisable();
        serverPortBind.setRouteTo(null);
        serverPortBind.setBindClientId(null);
        dbWrapper.updateByPrimaryKey(serverPortBind);


        return responseMessage;

    }


    /**
     * ip blackList
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.blackList)
    public PageListVO<IpFilterRule4V> blackList(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        PageDTO pageDTO = JSONUtils.str2Object(s, PageDTO.class);
        DBWrapper<IpFilterRule4V> dbWrapper = DBWrapper.getDBWrapper(IpFilterRule4V.class);
        PageResult<IpFilterRule4V> ipFilterRule4VPageResult = dbWrapper.customQueryByPage("select * from server_ip_filter_rule where type=1", pageDTO.getPage(), pageDTO.getRows());

        PageListVO<IpFilterRule4V> channelContextCloseRecordPageListVO = new PageListVO<>();
        channelContextCloseRecordPageListVO.setPage(pageDTO.getPage());
        channelContextCloseRecordPageListVO.setRows(pageDTO.getRows());
        channelContextCloseRecordPageListVO.setData(ipFilterRule4VPageResult.getData());
        channelContextCloseRecordPageListVO.setTotal(ipFilterRule4VPageResult.getTotal());
        return channelContextCloseRecordPageListVO;

    }


    /**
     * ip whiteList
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.whiteList)
    public PageListVO<IpFilterRule4V> whiteList(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        PageDTO pageDTO = JSONUtils.str2Object(s, PageDTO.class);
        DBWrapper<IpFilterRule4V> dbWrapper = DBWrapper.getDBWrapper(IpFilterRule4V.class);
        PageResult<IpFilterRule4V> ipFilterRule4VPageResult = dbWrapper.customQueryByPage("select * from server_ip_filter_rule where type=0", pageDTO.getPage(), pageDTO.getRows());

        PageListVO<IpFilterRule4V> channelContextCloseRecordPageListVO = new PageListVO<>();
        channelContextCloseRecordPageListVO.setPage(pageDTO.getPage());
        channelContextCloseRecordPageListVO.setRows(pageDTO.getRows());
        channelContextCloseRecordPageListVO.setData(ipFilterRule4VPageResult.getData());
        channelContextCloseRecordPageListVO.setTotal(ipFilterRule4VPageResult.getTotal());
        return channelContextCloseRecordPageListVO;

    }

    /**
     * addToIpWhiteList
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.addToIpWhiteList)
    public ResponseMessage addToIpWhiteList(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        IpDTO ipDTO = JSONUtils.str2Object(s, IpDTO.class);

        DBWrapper<IpFilterRule4V> dbWrapper = DBWrapper.getDBWrapper(IpFilterRule4V.class);
        //query exit white rule
        List<IpFilterRule4V> ipFilterRule4VS = dbWrapper.customQuery("select * from server_ip_filter_rule where type = 0 and ip = ?", ipDTO.getIp());
        if (ipFilterRule4VS.size() > 0) {
            responseMessage.error();
            responseMessage.setMessage("规则\"" + ipDTO.getIp() + "\"已存在");
            return responseMessage;
        }


        IpFilterRule4V ipFilterRule4V = new IpFilterRule4V();
        ipFilterRule4V.setId(UUIDSimple.id());
        ipFilterRule4V.white();
        ipFilterRule4V.setIp(ipDTO.getIp());


        //store into memory
        IpChecker bean = UniqueBeanManage.getBean(IpChecker.class);
        Map<String, IpFilterRule4V> whiteMap = bean.getWhiteMap();
        whiteMap.put(ipFilterRule4V.getIp(), ipFilterRule4V);

        //store into databases
        dbWrapper.insert(ipFilterRule4V);
        return responseMessage;

    }

    /**
     * addToIpBlackList
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.addToIpBlackList)
    public ResponseMessage addToIpBlackList(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        IpDTO ipDTO = JSONUtils.str2Object(s, IpDTO.class);

        DBWrapper<IpFilterRule4V> dbWrapper = DBWrapper.getDBWrapper(IpFilterRule4V.class);
        //query exit black rule
        List<IpFilterRule4V> ipFilterRule4VS = dbWrapper.customQuery("select * from server_ip_filter_rule where type = 1 and ip = ?", ipDTO.getIp());
        if (ipFilterRule4VS.size() > 0) {
            responseMessage.error();
            responseMessage.setMessage("规则\"" + ipDTO.getIp() + "\"已存在");
            return responseMessage;
        }
        IpFilterRule4V ipFilterRule4V = new IpFilterRule4V();
        ipFilterRule4V.setId(UUIDSimple.id());
        ipFilterRule4V.black();
        ipFilterRule4V.setIp(ipDTO.getIp());


        //store into memory
        IpChecker bean = UniqueBeanManage.getBean(IpChecker.class);
        Map<String, IpFilterRule4V> blackMap = bean.getBlackMap();
        blackMap.put(ipFilterRule4V.getIp(), ipFilterRule4V);


        //store into databases
        dbWrapper.insert(ipFilterRule4V);
        return responseMessage;
    }


    /**
     * deleteIpRule
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.deleteIpRuleByPrimaryKey)
    public ResponseMessage deleteIpRuleByPrimaryKey(JNDCHttpRequest jndcHttpRequest) {
        ResponseMessage responseMessage = new ResponseMessage();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        IpDTO ipDTO = JSONUtils.str2Object(s, IpDTO.class);

        DBWrapper<IpFilterRule4V> dbWrapper = DBWrapper.getDBWrapper(IpFilterRule4V.class);

        IpFilterRule4V ipFilterRule4V = dbWrapper.customQuerySingle("select * from server_ip_filter_rule where id=?", ipDTO.getId());
        if (ipFilterRule4V == null) {
            responseMessage.error();
            responseMessage.setMessage("规则\"" + ipDTO.getId() + "\"不存在");
            return responseMessage;
        }

        IpChecker ipChecker = UniqueBeanManage.getBean(IpChecker.class);
        if (ipFilterRule4V.isBlack()) {
            Map<String, IpFilterRule4V> blackMap = ipChecker.getBlackMap();
            blackMap.remove(ipFilterRule4V.getIp());

        } else {
            Map<String, IpFilterRule4V> whiteMap = ipChecker.getWhiteMap();
            whiteMap.remove(ipFilterRule4V.getIp());
        }

        dbWrapper.customExecute("delete from server_ip_filter_rule where id = ?", ipDTO.getId());
        return responseMessage;
    }


    /**
     * releaseRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.releaseRecord)
    public Object releaseRecord(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        PageDTO pageDTO = JSONUtils.str2Object(s, PageDTO.class);
        DBWrapper<IpFilterRecord> dbWrapper = DBWrapper.getDBWrapper(IpFilterRecord.class);
        PageResult<IpFilterRecord> ipFilterRecordPageResult = dbWrapper.customQueryByPage("select ip,max(time_stamp) timeStamp,sum(v_count) vCount from ip_filter_record where record_type=0 GROUP BY ip order by time_stamp desc", pageDTO.getPage(), pageDTO.getRows());
        List<IpRecordVO> ipRecordVOS = new ArrayList<>();
        ipFilterRecordPageResult.getData().forEach(x -> {
            IpRecordVO ipRecordVO = new IpRecordVO();
            ipRecordVO.setIp(x.getIp());
            ipRecordVO.setCount(x.getVCount());
            ipRecordVO.setLastTimeStamp(x.getTimeStamp());
            ipRecordVOS.add(ipRecordVO);
        });


        PageListVO<IpRecordVO> channelContextCloseRecordPageListVO = new PageListVO<>();
        channelContextCloseRecordPageListVO.setPage(pageDTO.getPage());
        channelContextCloseRecordPageListVO.setRows(pageDTO.getRows());
        channelContextCloseRecordPageListVO.setData(ipRecordVOS);
        channelContextCloseRecordPageListVO.setTotal(ipFilterRecordPageResult.getTotal());

        return channelContextCloseRecordPageListVO;

    }

    /**
     * releaseRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.blockRecord)
    public Object blockRecord(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        PageDTO pageDTO = JSONUtils.str2Object(s, PageDTO.class);

        DBWrapper<IpFilterRecord> dbWrapper = DBWrapper.getDBWrapper(IpFilterRecord.class);
        PageResult<IpFilterRecord> ipFilterRecordPageResult = dbWrapper.customQueryByPage("select ip,max(time_stamp) timeStamp,sum(v_count) vCount from ip_filter_record where record_type=1 GROUP BY ip order by time_stamp desc", pageDTO.getPage(), pageDTO.getRows());
        List<IpRecordVO> ipRecordVOS = new ArrayList<>();
        ipFilterRecordPageResult.getData().forEach(x -> {
            IpRecordVO ipRecordVO = new IpRecordVO();
            ipRecordVO.setIp(x.getIp());
            ipRecordVO.setCount(x.getVCount());
            ipRecordVO.setLastTimeStamp(x.getTimeStamp());
            ipRecordVOS.add(ipRecordVO);
        });


        PageListVO<IpRecordVO> channelContextCloseRecordPageListVO = new PageListVO<>();
        channelContextCloseRecordPageListVO.setPage(pageDTO.getPage());
        channelContextCloseRecordPageListVO.setRows(pageDTO.getRows());
        channelContextCloseRecordPageListVO.setData(ipRecordVOS);
        channelContextCloseRecordPageListVO.setTotal(ipFilterRecordPageResult.getTotal());

        return channelContextCloseRecordPageListVO;
    }

    /**
     * getCurrentDeviceIp
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = UrlConstant.ServerManage.getCurrentDeviceIp)
    public DeviceInfo getCurrentDeviceIp(JNDCHttpRequest jndcHttpRequest) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setIp(jndcHttpRequest.getRemoteAddress().getHostAddress());
        return deviceInfo;

    }

    @WebMapping(path = UrlConstant.ServerManage.clearIpRecord)
    public DeviceInfo clearIpRecord(JNDCHttpRequest jndcHttpRequest) {
        DeviceInfo deviceInfo = new DeviceInfo();
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ClearRecordOptionDTO pageDTO = JSONUtils.str2Object(s, ClearRecordOptionDTO.class);

        DBWrapper<IpFilterRecord> dbWrapper = DBWrapper.getDBWrapper(IpFilterRecord.class);
        if (pageDTO.clearByDateLimit()) {
            //todo clear by date limit
            dbWrapper.customExecute("delete from ip_filter_record where time_stamp<=? and record_type = ? ", pageDTO.getClearDateLimit(), pageDTO.getRecordType());
        } else {
            //todo save only top ten
            List<IpFilterRecord> ipFilterRecords = dbWrapper.customQuery("SELECT max(id) AS id, ip, recordType , max(time_stamp) AS timeStamp, max(v_count) AS vCount FROM ip_filter_record WHERE record_type = ? GROUP BY ip, record_type ORDER BY max(v_count) DESC LIMIT 10", pageDTO.getRecordType());
            dbWrapper.customExecute("delete from ip_filter_record where record_type = ? ", pageDTO.getRecordType());
            dbWrapper.insertBatch(ipFilterRecords);
        }


        LogPrint.info(pageDTO);
        return deviceInfo;

    }

}
