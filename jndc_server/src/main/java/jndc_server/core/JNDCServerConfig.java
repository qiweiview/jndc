package jndc_server.core;

import ch.qos.logback.classic.Level;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.DataStore;
import jndc.utils.*;
import jndc_server.databases_object.IpFilterRule4V;
import jndc_server.web_support.core.MappingRegisterCenter;
import jndc_server.web_support.core.MessageNotificationCenter;
import jndc_server.web_support.http_module.HostRouterComponent;
import jndc_server.web_support.utils.AuthUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    //管理页面api 端口
    private int managementApiPort;

    //管理页面api 端口
    private int servicePort;

    //运行绑定网卡
    private String bindIp;

    //黑名单
    private String[] blackList;

    //白名单
    private String[] whiteList;

    //http配置
    private ServeHTTPConfig webConfig;

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
        inetAddress = InetUtils.getByStringIpAddress(bindIp);
        inetSocketAddress = new InetSocketAddress(inetAddress, servicePort);
        httpInetSocketAddress = new InetSocketAddress(inetAddress, webConfig.getHttpPort());

        if (UN_SUPPORT_VALUE.equals(webConfig.getLoginName()) && UN_SUPPORT_VALUE.equals(webConfig.getLoginPassWord())) {
            LogPrint.err("the default name and password 'jndc' is not safe,please edit the config file and retry");
            ApplicationExit.exit();
        }
        AuthUtils.name = webConfig.getLoginName();
        AuthUtils.passWord = webConfig.getLoginPassWord();

        //perform ssl file
        performSslInWebApi();


        //register bean
        UniqueBeanManage.registerBean(this);

        UniqueBeanManage.registerBean(new NDCServerConfigCenter());
        UniqueBeanManage.registerBean(new IpChecker());
        UniqueBeanManage.registerBean(new MappingRegisterCenter());
        UniqueBeanManage.registerBean(new DataStore(getRuntimeDir()));
        AsynchronousEventCenter asynchronousEventCenter = new AsynchronousEventCenter();

        UniqueBeanManage.registerBean(asynchronousEventCenter);
        UniqueBeanManage.registerBean(new MessageNotificationCenter());
        UniqueBeanManage.registerBean(new ScheduledTaskCenter());
        UniqueBeanManage.registerBean(new HostRouterComponent());
        UniqueBeanManage.registerBean(new DataFlowAnalysisCenter(asynchronousEventCenter));

        //do server init
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.toLevel(getLoglevel()));

        //set secrete
        AESUtils.setKey(secrete.getBytes());


    }

    /**
     * 验证ssl
     */
    private void performSslInWebApi() {
        if (webConfig.isUseSsl()) {
            try {
                webConfig.reloadSslContext();
                log.info("open ssl in the web api");
            } catch (Exception e) {
                webConfig.setUseSsl(false);
                log.error("init ssl context  fail cause:" + e);
            }


        }
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
        if (blackList == null) {
            blackList = new String[0];
        }

        if (whiteList == null) {
            whiteList = new String[0];
        }

        List<IpFilterRule4V> storeList = new ArrayList<>();

        Stream.of(blackList).forEach(x -> {
            if (!blackMap.containsKey(x)) {

                IpFilterRule4V ipFilterRule4V = new IpFilterRule4V();
                ipFilterRule4V.black();
                ipFilterRule4V.setId(UUIDSimple.id());
                ipFilterRule4V.setIp(x);
                blackMap.put(x, ipFilterRule4V);
                storeList.add(ipFilterRule4V);
            }
        });

        Stream.of(whiteList).forEach(x -> {
            if (!whiteMap.containsKey(x)) {
                IpFilterRule4V ipFilterRule4V = new IpFilterRule4V();
                ipFilterRule4V.white();
                ipFilterRule4V.setId(UUIDSimple.id());
                ipFilterRule4V.setIp(x);
                whiteMap.put(x, ipFilterRule4V);
                storeList.add(ipFilterRule4V);
            }
        });

        if (storeList.size() > 0) {
            dbWrapper.insertBatch(storeList);
            log.info("add new ip filter rule:" + storeList);
        }

        ipChecker.loadRule(blackMap, whiteMap);
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "adminPort=" + servicePort +
                ", bindIp='" + bindIp + '\'' +
                '}';
    }


}
