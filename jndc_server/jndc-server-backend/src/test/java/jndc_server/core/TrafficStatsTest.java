package jndc_server.core;

import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.DataStoreAbstract;
import jndc.core.data_store_support.SQLiteDataStore;
import jndc_server.databases_object.ClientAuthRecord;
import jndc_server.web_support.mapping.ServerManageMapping;
import jndc_server.web_support.model.vo.ChannelContextVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TrafficStatsTest {

    @Before
    public void setUp() throws Exception {
        clearStaticMap(UniqueBeanManage.class, "map");
    }

    @After
    public void tearDown() throws Exception {
        clearStaticMap(UniqueBeanManage.class, "map");
    }

    @Test
    public void shouldTrackBandwidthWithinRollingWindow() throws Exception {
        registerDataStore();
        TCPDataFlowAnalysisCenter analysisCenter = registerAnalysisCenter();

        analysisCenter.analyse("client-a", messageOfSize(100), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, 0L);
        analysisCenter.analyse("client-a", messageOfSize(200), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, 1000L);
        analysisCenter.analyse("client-a", messageOfSize(300), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, 2000L);

        TCPDataFlowAnalysisCenter.TrafficSnapshot snapshot = analysisCenter.getTrafficSnapshot("client-a", true, 4000L);
        assertEquals(60L, snapshot.getClientToServerBandwidth());
        assertEquals(60L, snapshot.getServerToClientBandwidth());
        assertEquals(0L, snapshot.getPendingClientToServerBytes());
        assertEquals(0L, snapshot.getPendingServerToClientBytes());

        analysisCenter.analyse("client-a", messageOfSize(400), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, 6000L);
        TCPDataFlowAnalysisCenter.TrafficSnapshot rolledSnapshot = analysisCenter.getTrafficSnapshot("client-a", true, 6000L);
        assertEquals(80L, rolledSnapshot.getClientToServerBandwidth());
        assertEquals(60L, rolledSnapshot.getServerToClientBandwidth());

        TCPDataFlowAnalysisCenter.TrafficSnapshot offlineSnapshot = analysisCenter.getTrafficSnapshot("client-a", false, 6000L);
        assertEquals(0L, offlineSnapshot.getClientToServerBandwidth());
        assertEquals(0L, offlineSnapshot.getServerToClientBandwidth());
    }

    @Test
    public void shouldExposePersistedAndPendingTrafficInChannelTable() throws Exception {
        registerDataStore();
        NDCServerConfigCenter configCenter = new NDCServerConfigCenter();
        UniqueBeanManage.registerBean(configCenter);
        TCPDataFlowAnalysisCenter analysisCenter = registerAnalysisCenter();

        DBWrapper<ClientAuthRecord> dbWrapper = DBWrapper.getDBWrapper(ClientAuthRecord.class);

        ClientAuthRecord offlineRecord = new ClientAuthRecord();
        offlineRecord.setClientId("client-offline");
        offlineRecord.setClientAuthKey("offline-key");
        offlineRecord.setClientToServerBytes(33L);
        offlineRecord.setServerToClientBytes(44L);
        dbWrapper.insert(offlineRecord);

        ClientAuthRecord onlineRecord = new ClientAuthRecord();
        onlineRecord.setClientId("client-online");
        onlineRecord.setClientAuthKey("online-key");
        onlineRecord.setClientToServerBytes(100L);
        onlineRecord.setServerToClientBytes(200L);
        onlineRecord.setLastClientIp("10.0.0.8");
        onlineRecord.setLastClientPort(1081);
        onlineRecord.setLastSeenAt(12345L);
        dbWrapper.insert(onlineRecord);

        ChannelHandlerContextHolder onlineHolder = new ChannelHandlerContextHolder("client-online");
        onlineHolder.setAuthMode(1);
        onlineHolder.setContextIp("10.0.0.8");
        onlineHolder.setContextPort(1081);
        onlineHolder.setLastHearBeatTimeStamp(99999L);
        onlineHolder.setTcpServiceDescriptions(new CopyOnWriteArrayList<ServerServiceDescription>());
        onlineHolder.getTcpServiceDescriptions().add(new ServerServiceDescription());
        setOnlineHolder(configCenter, onlineHolder);

        long nowMillis = System.currentTimeMillis();
        analysisCenter.analyse("client-online", messageOfSize(50), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, nowMillis);
        analysisCenter.analyse("client-online", messageOfSize(70), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, nowMillis + 1);

        ServerManageMapping mapping = new ServerManageMapping();
        List<ChannelContextVO> table = mapping.getServerChannelTable(null);

        ChannelContextVO offline = findByClientId(table, "client-offline");
        assertFalse(offline.isOnline());
        assertEquals(33L, offline.getClientToServerBytes());
        assertEquals(44L, offline.getServerToClientBytes());
        assertEquals(0L, offline.getClientToServerBandwidth());
        assertEquals(0L, offline.getServerToClientBandwidth());

        ChannelContextVO online = findByClientId(table, "client-online");
        assertTrue(online.isOnline());
        assertEquals(150L, online.getClientToServerBytes());
        assertEquals(270L, online.getServerToClientBytes());
        assertEquals(10L, online.getClientToServerBandwidth());
        assertEquals(14L, online.getServerToClientBandwidth());
        assertEquals(nowMillis + 1, online.getTrafficUpdatedAt());
    }

    private TCPDataFlowAnalysisCenter registerAnalysisCenter() {
        TCPDataFlowAnalysisCenter analysisCenter = new TCPDataFlowAnalysisCenter(new ImmediateAsynchronousEventCenter());
        UniqueBeanManage.registerBean(analysisCenter);
        return analysisCenter;
    }

    private void registerDataStore() throws Exception {
        File runtimeDir = Files.createTempDirectory("jndc-traffic-test").toFile();
        runtimeDir.deleteOnExit();
        SQLiteDataStore dataStore = new SQLiteDataStore(runtimeDir.getAbsolutePath());
        dataStore.init();
        UniqueBeanManage.registerBean(DataStoreAbstract.class, dataStore);
    }

    private void setOnlineHolder(NDCServerConfigCenter configCenter, ChannelHandlerContextHolder holder) throws Exception {
        Field field = NDCServerConfigCenter.class.getDeclaredField("channelHandlerContextHolderMap");
        field.setAccessible(true);
        Map<String, ChannelHandlerContextHolder> holderMap = new ConcurrentHashMap<>();
        holderMap.put(holder.getClientId(), holder);
        field.set(configCenter, holderMap);
    }

    private ChannelContextVO findByClientId(List<ChannelContextVO> table, String clientId) {
        for (ChannelContextVO channelContextVO : table) {
            if (clientId.equals(channelContextVO.getClientId())) {
                return channelContextVO;
            }
        }
        throw new AssertionError("client not found: " + clientId);
    }

    private NDCMessageProtocol messageOfSize(int size) {
        NDCMessageProtocol protocol = new NDCMessageProtocol();
        protocol.setType(NDCMessageProtocol.TCP_DATA);
        protocol.setData(new byte[size]);
        return protocol;
    }

    private void clearStaticMap(Class<?> owner, String fieldName) throws Exception {
        Field field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        Map<?, ?> map = (Map<?, ?>) field.get(null);
        map.clear();
    }

    private static class ImmediateAsynchronousEventCenter extends AsynchronousEventCenter {
        @Override
        public void dataAnalyseJob(Runnable runnable) {
            runnable.run();
        }
    }
}
