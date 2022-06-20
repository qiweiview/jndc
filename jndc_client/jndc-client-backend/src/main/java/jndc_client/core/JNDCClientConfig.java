package jndc_client.core;

import ch.qos.logback.classic.Level;
import jndc.core.UniqueBeanManage;
import jndc.utils.AESUtils;
import jndc.utils.ApplicationExit;
import jndc.utils.InetUtils;
import jndc.utils.UUIDSimple;
import jndc_client.start.ClientStart;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JNDC client config
 */
@Data
@Slf4j
public class JNDCClientConfig {

    private static final String ID_CHARSET = "utf-8";

    private String secrete;

    private String loglevel;

    private String serverIp;

    private int serverPort;

//    private boolean openGui;

    //十分钟超时断开
    private long autoReleaseTimeOut = 10 * 60 * 1000;

    private List<ClientServiceDescription> clientServiceDescriptions;//service list

    private Map<String, ClientServiceDescription> clientServiceDescriptionMap;//service map


    /* -----------------prepare file----------------- */

    private InetAddress serverIpAddress;

    private InetSocketAddress serverIpSocketAddress;


    public void init() {
        //设置日志等级
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.toLevel(getLoglevel()));


        //set secrete
        AESUtils.setKey(secrete.getBytes());


        //注册实例：客户端配置中心
        UniqueBeanManage.registerBean(new JNDCClientConfigCenter());

        //初始化定时器
        initClientScheduled();

    }

    /**
     * 客户端定时器
     */
    private void initClientScheduled() {
        ClientScheduledTaskCenter clientScheduledTaskCenter = new ClientScheduledTaskCenter();
        UniqueBeanManage.registerBean(clientScheduledTaskCenter);
        clientScheduledTaskCenter.start();
    }

    /**
     * 参数校验
     */
    public void performParameterVerification() {


        serverIpAddress = InetUtils.getInetAddressByHost(serverIp);
        serverIpSocketAddress = new InetSocketAddress(serverIpAddress, serverPort);


        if (clientServiceDescriptions != null) {

            clientServiceDescriptionMap = new HashMap<>();
            clientServiceDescriptions.forEach(x -> {
                if (x.isServiceEnable()) {


                    if (clientServiceDescriptionMap.containsKey(x.getUniqueTag())) {
                        log.error("duplicate service:" + x.getUniqueTag());
                        ApplicationExit.exit();
                    }
                    x.performParameterVerification();
                    clientServiceDescriptionMap.put(x.getUniqueTag(), x);

                }
            });
        }

        //注册实例：客户端文件配置
        UniqueBeanManage.registerBean(this);


    /*   //应用场景不多，后续版本将废弃gui
        if (isOpenGui()) {
            GuiLogAppender.printIntoGui = true;
            new Thread(() -> {
                new GuiStart().start();
                log.info("init gui");
            }).start();
        }*/

        init();

    }


    /**
     * 装在本地客户端id
     */
    public void loadClientId() {
        File file = new File(ClientDirectManager.idPath);
        if (file.exists()) {
            //todo 读取已有id
            try {
                ClientStart.CLIENT_ID = FileUtils.readFileToString(file, ID_CHARSET);
            } catch (IOException e) {
                log.error("load client id fail,cause " + e);
                ApplicationExit.exit();
            }
        } else {
            //todo 创建新id
            ClientStart.CLIENT_ID = UUIDSimple.id();//随机生成
            try {
                FileUtils.writeStringToFile(file, ClientStart.CLIENT_ID, ID_CHARSET);
            } catch (IOException e) {
                log.error("load client id fail,cause " + e);
                ApplicationExit.exit();
            }

        }

    }
}
