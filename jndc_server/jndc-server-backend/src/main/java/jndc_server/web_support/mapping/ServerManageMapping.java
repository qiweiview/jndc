package jndc_server.web_support.mapping;


import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.PageResult;
import jndc.core.message.DeviceSummary;
import jndc.core.message.OpenChannelMessage;
import jndc.core.message.TcpServiceDescription;
import jndc.utils.JSONUtils;
import jndc.utils.LogPrint;
import jndc.utils.UUIDSimple;
import jndc.web_support.core.JNDCHttpRequest;
import jndc.web_support.core.MessageNotificationCenter;
import jndc.web_support.core.WebMapping;
import jndc.web_support.model.dto.LoginUser;
import jndc.web_support.model.dto.ResponseMessage;
import jndc_server.core.AsynchronousEventCenter;
import jndc_server.core.ChannelHandlerContextHolder;
import jndc_server.core.NDCServerConfigCenter;
import jndc_server.core.ServerServiceDescription;
import jndc_server.core.TCPDataFlowAnalysisCenter;
import jndc_server.core.filter.IpChecker;
import jndc_server.core.port_app.ServerPortProtector;
import jndc_server.databases_object.ClientAuthRecord;
import jndc_server.databases_object.ClientControlledServiceRecord;
import jndc_server.databases_object.ChannelContextCloseRecord;
import jndc_server.databases_object.IpFilterRecord;
import jndc_server.databases_object.IpFilterRule4V;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.model.dto.ChannelTrafficTrendDTO;
import jndc_server.web_support.model.dto.ClearRecordOptionDTO;
import jndc_server.web_support.model.dto.ControlledServiceReplaceDTO;
import jndc_server.web_support.model.dto.IpDTO;
import jndc_server.web_support.model.dto.PageDTO;
import jndc_server.web_support.model.dto.ServiceBindDTO;
import jndc_server.web_support.model.vo.ChannelContextVO;
import jndc_server.web_support.model.vo.ChannelTrafficTrendVO;
import jndc_server.web_support.model.vo.ServerRuntimeInfoVO;
import jndc_server.web_support.model.vo.ControlledServiceStateVO;
import jndc_server.web_support.model.vo.DeviceInfo;
import jndc_server.web_support.model.vo.IpRecordVO;
import jndc_server.web_support.model.vo.PageListVO;
import jndc_server.web_support.utils.AuthUtils;
import jndc_server.web_support.utils.ServerUrlConstant;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    @WebMapping(path = ServerUrlConstant.ServerManage.login)
    public HashMap login(JNDCHttpRequest jndcHttpRequest) {
        HashMap objectObjectHashMap = new HashMap<>();

        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        LoginUser managementLoginUser = JSONUtils.str2Object(s, LoginUser.class);
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
     * get the device channel list
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = ServerUrlConstant.ServerManage.getServerChannelTable)
    public List<ChannelContextVO> getServerChannelTable(JNDCHttpRequest jndcHttpRequest) {
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        TCPDataFlowAnalysisCenter trafficAnalysisCenter = UniqueBeanManage.getBean(TCPDataFlowAnalysisCenter.class);
        Map<String, ChannelContextVO> channelMap = new HashMap<>();
        Map<String, ClientAuthRecord> clientRecordMap = new HashMap<>();

        DBWrapper<ClientAuthRecord> dbWrapper = DBWrapper.getDBWrapper(ClientAuthRecord.class);
        List<ClientAuthRecord> clientAuthRecords = dbWrapper.customQuery("select * from client_auth_record");
        clientAuthRecords.forEach(record -> {
            clientRecordMap.put(record.getClientId(), record);
            ChannelContextVO channelContextVO = toChannelContext(record);
            applyTraffic(channelContextVO, record, trafficAnalysisCenter.getTrafficSnapshot(record.getClientId(), false));
            channelMap.put(record.getClientId(), channelContextVO);
        });

        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();
        channelHandlerContextHolders.forEach(x -> {
            ChannelContextVO facePortVO = channelMap.get(x.getClientId());
            if (facePortVO == null) {
                facePortVO = new ChannelContextVO();
            }
            facePortVO.setChannelId(x.getClientId());
            facePortVO.setClientId(x.getClientId());
            facePortVO.setServiceCount(x.serviceNum());
            facePortVO.setClientIp(x.getContextIp());
            facePortVO.setClientPort(x.getContextPort());
            facePortVO.setLastHeartbeat(x.getLastHearBeatTimeStamp());
            facePortVO.setLastSeenAt(x.getLastHearBeatTimeStamp());
            facePortVO.setLastOfflineAt(0L);
            facePortVO.setConnected(true);
            facePortVO.setOnline(true);
            facePortVO.setAuthMode(x.getAuthMode());
            applyTraffic(
                    facePortVO,
                    clientRecordMap.get(x.getClientId()),
                    trafficAnalysisCenter.getTrafficSnapshot(x.getClientId(), true)
            );
            channelMap.put(x.getClientId(), facePortVO);
        });

        List<ChannelContextVO> list = new ArrayList<>(channelMap.values());
        list.sort(Comparator
                .comparing(ChannelContextVO::isOnline)
                .reversed()
                .thenComparing(ChannelContextVO::getLastSeenAt, Comparator.reverseOrder())
                .thenComparing(ChannelContextVO::getClientId, Comparator.nullsLast(String::compareTo)));
        return list;

    }

    /**
     * getChannelRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = ServerUrlConstant.ServerManage.getChannelRecord)
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

    @WebMapping(path = ServerUrlConstant.ServerManage.getRecentChannelRecordByClientId)
    public List<ChannelContextCloseRecord> getRecentChannelRecordByClientId(JNDCHttpRequest jndcHttpRequest) {
        ChannelContextVO channelContextVO = jndcHttpRequest.getObject(ChannelContextVO.class);
        String clientId = resolveChannelId(channelContextVO);
        if (clientId == null || "".equals(clientId.trim())) {
            return new ArrayList<>();
        }

        DBWrapper<ChannelContextCloseRecord> dbWrapper = DBWrapper.getDBWrapper(ChannelContextCloseRecord.class);
        return dbWrapper.customQuery(
                "select * from channel_context_record where client_id=? order by time_stamp desc limit 10",
                clientId
        );
    }

    @WebMapping(path = ServerUrlConstant.ServerManage.getChannelTrafficTrend)
    public ChannelTrafficTrendVO getChannelTrafficTrend(JNDCHttpRequest jndcHttpRequest) {
        ChannelTrafficTrendDTO trendDTO = jndcHttpRequest.getObject(ChannelTrafficTrendDTO.class);
        TCPDataFlowAnalysisCenter trafficAnalysisCenter = UniqueBeanManage.getBean(TCPDataFlowAnalysisCenter.class);
        if (trendDTO == null) {
            return trafficAnalysisCenter.getTrafficTrend(null, null);
        }
        return trafficAnalysisCenter.getTrafficTrend(trendDTO.getClientId(), trendDTO.getRange());
    }


    /**
     * clearChannelRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = ServerUrlConstant.ServerManage.clearChannelRecord)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.sendHeartBeat)
    public ResponseMessage sendHeartBeat(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ChannelContextVO channelContextVO = JSONUtils.str2Object(s, ChannelContextVO.class);
        String id = resolveChannelId(channelContextVO);

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
    @WebMapping(path = ServerUrlConstant.ServerManage.closeChannelByServer)
    public ResponseMessage closeChannelByServer(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ChannelContextVO channelContextVO = JSONUtils.str2Object(s, ChannelContextVO.class);
        String id = resolveChannelId(channelContextVO);

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
    @WebMapping(path = ServerUrlConstant.ServerManage.getServiceList)
    public List<ServerServiceDescription> getServiceList(JNDCHttpRequest jndcHttpRequest) {

        ServerServiceDescription param = jndcHttpRequest.getObject(ServerServiceDescription.class);
        NDCServerConfigCenter bean = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        List<ChannelHandlerContextHolder> channelHandlerContextHolders = bean.getChannelHandlerContextHolders();
        List<ServerServiceDescription> collect = channelHandlerContextHolders.stream().flatMap(x -> {
            List<ServerServiceDescription> tcpServiceDescriptions1 = x.getTcpServiceDescriptions();
            return tcpServiceDescriptions1.stream().filter(z -> {
                //todo 过滤
                String bindClientId = z.getBindClientId();
                return param.getBindClientId() == null || "".equals(param.getBindClientId()) || bindClientId.equals(param.getBindClientId());
            });
        }).collect(Collectors.toList());
        return collect;

    }

    @WebMapping(path = ServerUrlConstant.ServerManage.getClientControlledServiceList)
    public ResponseMessage getClientControlledServiceList(JNDCHttpRequest jndcHttpRequest) {
        ChannelContextVO channelContextVO = jndcHttpRequest.getObject(ChannelContextVO.class);
        String clientId = resolveChannelId(channelContextVO);
        if (clientId == null || "".equals(clientId.trim())) {
            return ResponseMessage.fail("clientId 不能为空");
        }

        NDCServerConfigCenter configCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ChannelHandlerContextHolder holder = configCenter.getContextHolder(clientId);
        ClientAuthRecord clientAuthRecord = DBWrapper.getDBWrapper(ClientAuthRecord.class)
                .customQuerySingle("select * from client_auth_record where client_id=?", clientId);

        ControlledServiceStateVO stateVO = new ControlledServiceStateVO();
        stateVO.setClientId(clientId);
        stateVO.setOnline(holder != null);
        stateVO.setAuthMode(holder != null ? holder.getAuthMode() : getAuthMode(clientAuthRecord));
        stateVO.setTargetServices(loadControlledServiceDescriptions(clientId));
        stateVO.setActualServices(holder == null ? new ArrayList<>() : holder.getTcpServiceDescriptions().stream()
                .map(this::toTcpServiceDescription)
                .collect(Collectors.toList()));
        return ResponseMessage.success(stateVO);
    }

    @WebMapping(path = ServerUrlConstant.ServerManage.replaceClientControlledServices)
    public ResponseMessage replaceClientControlledServices(JNDCHttpRequest jndcHttpRequest) {
        ControlledServiceReplaceDTO replaceDTO = jndcHttpRequest.getObject(ControlledServiceReplaceDTO.class);
        String clientId = replaceDTO.getClientId();
        if (clientId == null || "".equals(clientId.trim())) {
            return ResponseMessage.fail("clientId 不能为空");
        }

        NDCServerConfigCenter configCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        ChannelHandlerContextHolder holder = configCenter.getContextHolder(clientId);
        ClientAuthRecord clientAuthRecord = DBWrapper.getDBWrapper(ClientAuthRecord.class)
                .customQuerySingle("select * from client_auth_record where client_id=?", clientId);
        int authMode = holder != null ? holder.getAuthMode() : getAuthMode(clientAuthRecord);
        if (authMode != OpenChannelMessage.FULL_AUTHORIZED) {
            return ResponseMessage.fail("当前 client 不处于全授权模式");
        }

        List<TcpServiceDescription> services = replaceDTO.getServices() == null ? new ArrayList<>() : replaceDTO.getServices();
        ResponseMessage validation = validateControlledServices(services);
        if (validation != null) {
            return validation;
        }
        persistControlledServices(clientId, services);

        if (holder != null && holder.getAuthMode() == OpenChannelMessage.FULL_AUTHORIZED) {
            configCenter.applyControlledServices(clientId, services);
        } else {
            MessageNotificationCenter messageNotificationCenter = UniqueBeanManage.getBean(MessageNotificationCenter.class);
            messageNotificationCenter.dateRefreshMessage("serviceControl");
        }

        return new ResponseMessage();
    }

    private ResponseMessage validateControlledServices(List<TcpServiceDescription> services) {
        Set<String> serviceKeys = new HashSet<>();
        for (TcpServiceDescription service : services) {
            if (service == null) {
                return ResponseMessage.fail("服务项不能为空");
            }
            if (service.getServiceName() == null || "".equals(service.getServiceName().trim())) {
                return ResponseMessage.fail("服务名称不能为空");
            }
            if (service.getServiceIp() == null || "".equals(service.getServiceIp().trim())) {
                return ResponseMessage.fail("服务IP不能为空");
            }
            if (service.getServicePort() <= 0 || service.getServicePort() > 65535) {
                return ResponseMessage.fail("服务端口不合法");
            }
            String serviceKey = service.getServiceIp().trim() + ":" + service.getServicePort();
            if (!serviceKeys.add(serviceKey)) {
                return ResponseMessage.fail("存在重复服务: " + serviceKey);
            }
        }
        return null;
    }

    private void persistControlledServices(String clientId, List<TcpServiceDescription> services) {
        DBWrapper<ClientControlledServiceRecord> dbWrapper = DBWrapper.getDBWrapper(ClientControlledServiceRecord.class);
        dbWrapper.customExecute("delete from client_controlled_service where client_id=?", clientId);

        services.forEach(service -> {
            ClientControlledServiceRecord record = new ClientControlledServiceRecord();
            record.setId(UUIDSimple.id());
            record.setClientId(clientId);
            record.setServiceName(service.getServiceName());
            record.setServiceIp(service.getServiceIp());
            record.setServicePort(service.getServicePort());
            record.setDescription(service.getDescription());
            dbWrapper.insert(record);
        });
    }

    private List<TcpServiceDescription> loadControlledServiceDescriptions(String clientId) {
        DBWrapper<ClientControlledServiceRecord> dbWrapper = DBWrapper.getDBWrapper(ClientControlledServiceRecord.class);
        List<ClientControlledServiceRecord> records = dbWrapper.customQuery(
                "select * from client_controlled_service where client_id=? order by service_name asc, service_ip asc, service_port asc",
                clientId
        );
        return records.stream().map(this::toTcpServiceDescription).collect(Collectors.toList());
    }

    private TcpServiceDescription toTcpServiceDescription(ClientControlledServiceRecord record) {
        TcpServiceDescription tcpServiceDescription = new TcpServiceDescription();
        tcpServiceDescription.setId(record.getId());
        tcpServiceDescription.setServiceName(record.getServiceName());
        tcpServiceDescription.setServiceIp(record.getServiceIp());
        tcpServiceDescription.setServicePort(record.getServicePort());
        tcpServiceDescription.setDescription(record.getDescription());
        return tcpServiceDescription;
    }

    private TcpServiceDescription toTcpServiceDescription(ServerServiceDescription record) {
        TcpServiceDescription tcpServiceDescription = new TcpServiceDescription();
        tcpServiceDescription.setId(record.getId());
        tcpServiceDescription.setServiceName(record.getServiceName());
        tcpServiceDescription.setServiceIp(record.getServiceIp());
        tcpServiceDescription.setServicePort(record.getServicePort());
        tcpServiceDescription.setDescription(record.getDescription());
        return tcpServiceDescription;
    }

    private int getAuthMode(ClientAuthRecord clientAuthRecord) {
        if (clientAuthRecord == null || clientAuthRecord.getAuthMode() == null) {
            return OpenChannelMessage.SELF_MANAGED;
        }
        return clientAuthRecord.getAuthMode();
    }

    private ChannelContextVO toChannelContext(ClientAuthRecord clientAuthRecord) {
        ChannelContextVO channelContextVO = new ChannelContextVO();
        channelContextVO.setChannelId(clientAuthRecord.getClientId());
        channelContextVO.setClientId(clientAuthRecord.getClientId());
        channelContextVO.setServiceCount(0);
        channelContextVO.setClientIp(defaultString(clientAuthRecord.getLastClientIp()));
        channelContextVO.setClientPort(clientAuthRecord.getLastClientPort() == null ? 0 : clientAuthRecord.getLastClientPort());
        channelContextVO.setLastHeartbeat(clientAuthRecord.getLastSeenAt() == null ? 0L : clientAuthRecord.getLastSeenAt());
        channelContextVO.setLastSeenAt(clientAuthRecord.getLastSeenAt() == null ? 0L : clientAuthRecord.getLastSeenAt());
        channelContextVO.setLastOfflineAt(clientAuthRecord.getLastOfflineAt() == null ? 0L : clientAuthRecord.getLastOfflineAt());
        channelContextVO.setConnected(false);
        channelContextVO.setOnline(false);
        channelContextVO.setAuthMode(getAuthMode(clientAuthRecord));

        DeviceSummary deviceSummary = clientAuthRecord.toDeviceSummary();
        channelContextVO.setOsName(defaultString(deviceSummary.getOsName()));
        channelContextVO.setOsVersion(defaultString(deviceSummary.getOsVersion()));
        channelContextVO.setCpuModel(defaultString(deviceSummary.getCpuModel()));
        channelContextVO.setCpuLogicalCores(deviceSummary.getCpuLogicalCores());
        channelContextVO.setGpuNames(deviceSummary.getGpuNames());
        channelContextVO.setMemoryTotalBytes(deviceSummary.getMemoryTotalBytes());
        channelContextVO.setDiskTotalBytes(deviceSummary.getDiskTotalBytes());
        channelContextVO.setDiskFreeBytes(deviceSummary.getDiskFreeBytes());
        channelContextVO.setClientToServerBytes(defaultLong(clientAuthRecord.getClientToServerBytes()));
        channelContextVO.setServerToClientBytes(defaultLong(clientAuthRecord.getServerToClientBytes()));
        channelContextVO.setClientToServerBandwidth(0L);
        channelContextVO.setServerToClientBandwidth(0L);
        channelContextVO.setTrafficUpdatedAt(0L);
        return channelContextVO;
    }

    private void applyTraffic(ChannelContextVO channelContextVO, ClientAuthRecord clientAuthRecord, TCPDataFlowAnalysisCenter.TrafficSnapshot trafficSnapshot) {
        long persistedClientToServer = clientAuthRecord == null ? 0L : defaultLong(clientAuthRecord.getClientToServerBytes());
        long persistedServerToClient = clientAuthRecord == null ? 0L : defaultLong(clientAuthRecord.getServerToClientBytes());
        TCPDataFlowAnalysisCenter.TrafficSnapshot snapshot = trafficSnapshot == null
                ? TCPDataFlowAnalysisCenter.TrafficSnapshot.empty()
                : trafficSnapshot;
        channelContextVO.setClientToServerBytes(persistedClientToServer + snapshot.getPendingClientToServerBytes());
        channelContextVO.setServerToClientBytes(persistedServerToClient + snapshot.getPendingServerToClientBytes());
        channelContextVO.setClientToServerBandwidth(snapshot.getClientToServerBandwidth());
        channelContextVO.setServerToClientBandwidth(snapshot.getServerToClientBandwidth());
        channelContextVO.setTrafficUpdatedAt(snapshot.getTrafficUpdatedAt());
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private String resolveChannelId(ChannelContextVO channelContextVO) {
        if (channelContextVO == null) {
            return null;
        }
        if (channelContextVO.getChannelId() != null && !"".equals(channelContextVO.getChannelId().trim())) {
            return channelContextVO.getChannelId();
        }
        return channelContextVO.getClientId();
    }


    /**
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = ServerUrlConstant.ServerManage.getServerPortList)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.createPortMonitoring)
    public ResponseMessage createPortMonitoring(JNDCHttpRequest jndcHttpRequest) {


        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        ServiceBindDTO createPortDTO = JSONUtils.str2Object(s, ServiceBindDTO.class);

        ServerPortBind channelContextVO = new ServerPortBind();
        channelContextVO.setPort(createPortDTO.getPort());
        channelContextVO.setEnableDateRange(resolveEnableDateRange(createPortDTO));


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
    @WebMapping(path = ServerUrlConstant.ServerManage.doServiceBind)
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
                        boolean matchedByServiceId = channelContextVO.getServiceId() != null
                                && channelContextVO.getServiceId().equals(service.getId());
                        boolean matchedByRouteTo = channelContextVO.getRouteTo() != null
                                && channelContextVO.getRouteTo().equals(service.getRouteTo());
                        if (matchedByServiceId || matchedByRouteTo) {//find the service from contextHolder


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
    @WebMapping(path = ServerUrlConstant.ServerManage.doDateRangeEdit)
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
            serverPortProtector.parseEnableDateRange(resolveEnableDateRange(channelContextVO));

            //reset all connection
            serverPortProtector.resetAllConnection();

        } else {
            log.debug("can not found the service on server port " + serverPortBind.getPort());
        }

        //do db info update
        serverPortBind.setEnableDateRange(resolveEnableDateRange(channelContextVO));
        serverPortBind.setName(channelContextVO.getRemark());
        dbWrapper.updateByPrimaryKey(serverPortBind);

        return responseMessage;

    }

    private String resolveEnableDateRange(ServiceBindDTO serviceBindDTO) {
        String enableDateRange = serviceBindDTO.resolveEnableDateRange();
        if (enableDateRange == null || enableDateRange.trim().isEmpty()) {
            return "00:00:00,23:59:59";
        }
        return enableDateRange;
    }


    /**
     * deleteServiceBindRecord
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = ServerUrlConstant.ServerManage.deleteServiceBindRecord)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.resetBindRecord)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.stopServiceBind)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.blackList)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.whiteList)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.addToIpWhiteList)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.addToIpBlackList)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.deleteIpRuleByPrimaryKey)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.releaseRecord)
    public Object releaseRecord(JNDCHttpRequest jndcHttpRequest) {
        byte[] body = jndcHttpRequest.getBody();
        String s = new String(body);
        PageDTO pageDTO = JSONUtils.str2Object(s, PageDTO.class);
        DBWrapper<IpFilterRecord> dbWrapper = DBWrapper.getDBWrapper(IpFilterRecord.class);
        PageResult<IpFilterRecord> ipFilterRecordPageResult = dbWrapper.customQueryByPage("select ip,max(time_stamp) timeStamp,sum(v_count) vCount from ip_filter_record where record_type=0 GROUP BY ip order by max( time_stamp ) desc", pageDTO.getPage(), pageDTO.getRows());
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
    @WebMapping(path = ServerUrlConstant.ServerManage.blockRecord)
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
    @WebMapping(path = ServerUrlConstant.ServerManage.getCurrentDeviceIp)
    public DeviceInfo getCurrentDeviceIp(JNDCHttpRequest jndcHttpRequest) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setIp(jndcHttpRequest.getRemoteAddress().getHostAddress());
        return deviceInfo;

    }

    @WebMapping(path = ServerUrlConstant.ServerManage.clearIpRecord)
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
            List<IpFilterRecord> ipFilterRecords = dbWrapper.customQuery("SELECT max(id) AS id, ip, record_type as \"recordType\" , max(time_stamp) AS timeStamp, max(v_count) AS vCount FROM ip_filter_record WHERE record_type = ? GROUP BY ip, record_type ORDER BY max(v_count) DESC LIMIT 10", pageDTO.getRecordType());
            dbWrapper.customExecute("delete from ip_filter_record where record_type = ? ", pageDTO.getRecordType());
            dbWrapper.insertBatch(ipFilterRecords);
        }


        LogPrint.info(pageDTO);
        return deviceInfo;
    }

    /**
     * get server runtime config info
     *
     * @param jndcHttpRequest
     * @return
     */
    @WebMapping(path = ServerUrlConstant.ServerManage.getServerRuntimeInfo)
    public ServerRuntimeInfoVO getServerRuntimeInfo(JNDCHttpRequest jndcHttpRequest) {
        jndc_server.config.JNDCServerConfig config = UniqueBeanManage.getBean(jndc_server.config.JNDCServerConfig.class);
        ServerRuntimeInfoVO vo = new ServerRuntimeInfoVO();
        if (config != null) {
            vo.setBindIp(config.getBindIp());
            vo.setServicePort(config.getServicePort());
            vo.setSecrete(config.getSecrete());
            if (config.getManageConfig() != null) {
                vo.setManagementApiPort(config.getManageConfig().getManagementApiPort());
            }
            if (config.getWebConfig() != null) {
                vo.setHttpPort(config.getWebConfig().getHttpPort());
            }
        }
        return vo;
    }
}
