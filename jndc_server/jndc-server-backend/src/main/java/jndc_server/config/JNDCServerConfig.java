package jndc_server.config;

import ch.qos.logback.classic.Level;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.DataStoreAbstract;
import jndc.core.data_store_support.SQLiteDataStore;
import jndc.utils.*;
import jndc.web_support.config.ServeHTTPConfig;
import jndc.web_support.config.ServeManageConfig;
import jndc.web_support.core.MappingRegisterCenter;
import jndc.web_support.core.MessageNotificationCenter;
import jndc_server.core.AsynchronousEventCenter;
import jndc_server.core.NDCServerConfigCenter;
import jndc_server.core.RuntimeDataCleanupService;
import jndc_server.core.ScheduledTaskCenter;
import jndc_server.core.ServerTerminalSessionManager;
import jndc_server.core.TCPDataFlowAnalysisCenter;
import jndc_server.core.filter.IpChecker;
import jndc_server.databases_object.IpFilterRule4V;
import jndc_server.web_support.http_module.HostRouterComponent;
import jndc_server.web_support.mapping.DevelopDebugMapping;
import jndc_server.web_support.mapping.ServerHttpManageMapping;
import jndc_server.web_support.mapping.ServerManageMapping;
import jndc_server.web_support.utils.AuthUtils;
import jndc_server.web_support.websocket.ServerWebSocketDispatcher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class JNDCServerConfig {

    private static final String UN_SUPPORT_VALUE = "jndc";

    //运行目录
    private String runtimeDir = "";

    //密码
    private String secrete;

    //日志等级
    private String loglevel;

    //运行时数据清理配置
    private DataCleanupConfig cleanupConfig = new DataCleanupConfig();


    //核心服务端口
    private int servicePort;

    //运行绑定网卡
    private String bindIp;

    //http配置
    private ServeHTTPConfig webConfig;

    //管理api配置
    private ServeManageConfig manageConfig;

    /* ------------非配置属性------------ */


    private InetAddress inetAddress;

    private InetSocketAddress inetSocketAddress;

    private InetSocketAddress httpInetSocketAddress;


    static {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    /**
     * 参数校验
     */
    public void performParameterVerification() {
        if (cleanupConfig == null) {
            cleanupConfig = new DataCleanupConfig();
        }
        inetAddress = InetUtils.getByStringIpAddress(bindIp);
        inetSocketAddress = new InetSocketAddress(inetAddress, servicePort);
        httpInetSocketAddress = new InetSocketAddress(inetAddress, webConfig.getHttpPort());

        //验证账号密码是否默认
        if (UN_SUPPORT_VALUE.equals(manageConfig.getLoginName()) && UN_SUPPORT_VALUE.equals(manageConfig.getLoginPassWord())) {
            LogPrint.err("the default name and password 'jndc' is not safe,please edit the config file and retry");
            ApplicationExit.exit();
        }

        //设置登录用户名密码
        AuthUtils.name = manageConfig.getLoginName();
        AuthUtils.passWord = manageConfig.getLoginPassWord();

        //验证ssl文件
        performSslInWebApi();

        //注册对象
        UniqueBeanManage.registerBean(this);

        //配置中心
        UniqueBeanManage.registerBean(new NDCServerConfigCenter());

        //IP过滤
        UniqueBeanManage.registerBean(new IpChecker());

        //隧道映射注册
        MappingRegisterCenter mappingRegisterCenter = new MappingRegisterCenter();
        mappingRegisterCenter.registerMapping(new DevelopDebugMapping());
        mappingRegisterCenter.registerMapping(new ServerHttpManageMapping());
        mappingRegisterCenter.registerMapping(new ServerManageMapping());
        UniqueBeanManage.registerBean(mappingRegisterCenter);

        //数据存储组件
        DataStoreAbstract sqLiteDataStore = new SQLiteDataStore(getRuntimeDir());
        sqLiteDataStore.init();
        performSchemaMigration(sqLiteDataStore);
        UniqueBeanManage.registerBean(DataStoreAbstract.class, sqLiteDataStore);
        UniqueBeanManage.registerBean(new RuntimeDataCleanupService(this, sqLiteDataStore));
        log.info("使用sqlite数据库存储");


        //异步执行中心
        AsynchronousEventCenter asynchronousEventCenter = new AsynchronousEventCenter();
        UniqueBeanManage.registerBean(asynchronousEventCenter);

        //websocket消息通知中心
        UniqueBeanManage.registerBean(new MessageNotificationCenter());
        UniqueBeanManage.registerBean(new ServerTerminalSessionManager());
        UniqueBeanManage.registerBean(jndc.web_support.core.WebSocketEventDispatcher.class, new ServerWebSocketDispatcher());

        //定时执行
        UniqueBeanManage.registerBean(new ScheduledTaskCenter());

        //域名路由
        UniqueBeanManage.registerBean(new HostRouterComponent());

        //流量分析
        UniqueBeanManage.registerBean(new TCPDataFlowAnalysisCenter(asynchronousEventCenter));

        //初始化
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        //设置日志等级
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.toLevel(getLoglevel()));

        //set secrete
        AESUtils.setKey(secrete.getBytes());


    }

    /**
     * 验证ssl
     */
    private void performSslInWebApi() {
        webConfig.initSsl();

        manageConfig.initSsl();
    }


    //fix tag

    /**
     * lazy init
     */
    public void lazyInitAfterVerification() {
        initIpChecker();
    }


    /**
     * 初始化ip检测器
     */
    private void initIpChecker() {
        DBWrapper<IpFilterRule4V> dbWrapper = DBWrapper.getDBWrapper(IpFilterRule4V.class);
        List<IpFilterRule4V> ipFilterRule4VS = dbWrapper.listAll();
        Map<String, IpFilterRule4V> blackMap = new HashMap<>();
        Map<String, IpFilterRule4V> whiteMap = new HashMap<>();
        ipFilterRule4VS.forEach(x -> {
            if (x.isBlack()) {
                blackMap.put(x.getIp(), x);
            } else {
                whiteMap.put(x.getIp(), x);
            }
        });

        IpChecker ipChecker = UniqueBeanManage.getBean(IpChecker.class);
        ipChecker.loadRule(blackMap, whiteMap);
    }

    private void performSchemaMigration(DataStoreAbstract dataStoreAbstract) {
        addChannelRecordColumn(dataStoreAbstract, "client_id", "text(255)");
        addChannelRecordColumn(dataStoreAbstract, "disconnect_reason", "text(255)");
        try {
            dataStoreAbstract.execute(
                    "delete from channel_context_record where client_id is null or client_id=''",
                    null
            );
        } catch (RuntimeException e) {
            log.debug("skip channel_context_record cleanup: {}", e.getMessage());
        }
        addClientAuthColumn(dataStoreAbstract, "auth_mode", "integer");
        addClientAuthColumn(dataStoreAbstract, "last_client_ip", "text(255)");
        addClientAuthColumn(dataStoreAbstract, "last_client_port", "integer");
        addClientAuthColumn(dataStoreAbstract, "last_seen_at", "bigint");
        addClientAuthColumn(dataStoreAbstract, "last_offline_at", "bigint");
        addClientAuthColumn(dataStoreAbstract, "os_name", "text(255)");
        addClientAuthColumn(dataStoreAbstract, "os_version", "text(255)");
        addClientAuthColumn(dataStoreAbstract, "cpu_model", "text(255)");
        addClientAuthColumn(dataStoreAbstract, "cpu_logical_cores", "integer");
        addClientAuthColumn(dataStoreAbstract, "gpu_names", "text(2000)");
        addClientAuthColumn(dataStoreAbstract, "memory_total_bytes", "bigint");
        addClientAuthColumn(dataStoreAbstract, "disk_total_bytes", "bigint");
        addClientAuthColumn(dataStoreAbstract, "disk_free_bytes", "bigint");
        addClientAuthColumn(dataStoreAbstract, "client_to_server_bytes", "bigint");
        addClientAuthColumn(dataStoreAbstract, "server_to_client_bytes", "bigint");
        ensureTrafficTrendTable(dataStoreAbstract);
        ensureMaintenanceIndexes(dataStoreAbstract);
    }

    private void addClientAuthColumn(DataStoreAbstract dataStoreAbstract, String columnName, String columnType) {
        try {
            dataStoreAbstract.execute(
                    "alter table client_auth_record add column " + columnName + " " + columnType,
                    null
            );
        } catch (RuntimeException e) {
            log.debug("skip {} migration: {}", columnName, e.getMessage());
        }
    }

    private void addChannelRecordColumn(DataStoreAbstract dataStoreAbstract, String columnName, String columnType) {
        try {
            dataStoreAbstract.execute(
                    "alter table channel_context_record add column " + columnName + " " + columnType,
                    null
            );
        } catch (RuntimeException e) {
            log.debug("skip channel_context_record.{} migration: {}", columnName, e.getMessage());
        }
    }

    private void ensureTrafficTrendTable(DataStoreAbstract dataStoreAbstract) {
        try {
            dataStoreAbstract.execute(
                    "CREATE TABLE IF NOT EXISTS client_traffic_trend_record (" +
                            "id text(255) NOT NULL," +
                            "client_id text(255) NOT NULL," +
                            "bucket_type text(32) NOT NULL," +
                            "bucket_start_at bigint NOT NULL," +
                            "client_to_server_bytes bigint," +
                            "server_to_client_bytes bigint," +
                            "updated_at bigint," +
                            "PRIMARY KEY (id)" +
                            ")",
                    null
            );
        } catch (RuntimeException e) {
            log.debug("skip client_traffic_trend_record migration: {}", e.getMessage());
        }
    }

    private void ensureMaintenanceIndexes(DataStoreAbstract dataStoreAbstract) {
        createIndexIfAbsent(
                dataStoreAbstract,
                "create index if not exists idx_channel_context_record_time_stamp on channel_context_record(time_stamp)"
        );
        createIndexIfAbsent(
                dataStoreAbstract,
                "create index if not exists idx_channel_context_record_client_time on channel_context_record(client_id, time_stamp)"
        );
        createIndexIfAbsent(
                dataStoreAbstract,
                "create index if not exists idx_ip_filter_record_type_time on ip_filter_record(record_type, time_stamp)"
        );
        createIndexIfAbsent(
                dataStoreAbstract,
                "create index if not exists idx_client_traffic_trend_bucket_time on client_traffic_trend_record(bucket_type, bucket_start_at)"
        );
        createIndexIfAbsent(
                dataStoreAbstract,
                "create index if not exists idx_client_traffic_trend_client_bucket_time on client_traffic_trend_record(client_id, bucket_type, bucket_start_at)"
        );
    }

    private void createIndexIfAbsent(DataStoreAbstract dataStoreAbstract, String sql) {
        try {
            dataStoreAbstract.execute(sql, null);
        } catch (RuntimeException e) {
            log.debug("skip index migration: {}", e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "adminPort=" + servicePort +
                ", bindIp='" + bindIp + '\'' +
                '}';
    }


}
