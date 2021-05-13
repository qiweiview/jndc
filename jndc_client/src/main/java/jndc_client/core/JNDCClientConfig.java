package jndc_client.core;

import ch.qos.logback.classic.Level;
import jndc.core.UniqueBeanManage;
import jndc.utils.AESUtils;
import jndc.utils.ApplicationExit;
import jndc.utils.InetUtils;
import jndc.utils.UUIDSimple;
import jndc_client.gui_support.GuiStart;
import jndc_client.gui_support.utils.GuiLogAppender;
import jndc_client.start.ClientStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JNDC client config
 */
public class JNDCClientConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String runtimeDir = "";

    private String secrete;

    private String loglevel;

    private String serverIp;

    private int serverPort;

    private boolean openGui;

    private List<ClientServiceDescription> clientServiceDescriptions;//service list

    private Map<String, ClientServiceDescription> clientServiceDescriptionMap;//service map


    /* -----------------prepare file----------------- */

    private InetAddress serverIpAddress;

    private InetSocketAddress serverIpSocketAddress;


    public void init() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.toLevel(getLoglevel()));


        //set secrete
        AESUtils.setKey(secrete.getBytes());


        //register bean,this will be replaced later
        UniqueBeanManage.registerBean(new JNDCClientConfigCenter());
    }

    /**
     * 参数校验
     */
    public void performParameterVerification() {


        serverIpAddress = InetUtils.getByStringIpAddress(serverIp);
        serverIpSocketAddress = new InetSocketAddress(serverIpAddress, serverPort);


        if (clientServiceDescriptions != null) {

            clientServiceDescriptionMap = new HashMap<>();
            clientServiceDescriptions.forEach(x -> {
                if (x.isServiceEnable()) {


                    if (clientServiceDescriptionMap.containsKey(x.getUniqueTag())) {
                        logger.error("duplicate service:" + x.getUniqueTag());
                        ApplicationExit.exit();
                    }
                    x.performParameterVerification();
                    clientServiceDescriptionMap.put(x.getUniqueTag(), x);

                }
            });
        }


        UniqueBeanManage.registerBean(this);


        //check open gui or not
        if (isOpenGui()) {
            GuiLogAppender.printIntoGui = true;
            new Thread(() -> {
                new GuiStart().start();
                logger.info("init gui");
            }).start();
        }

        init();

    }


    //getter setter


    public String getRuntimeDir() {
        return runtimeDir;
    }

    public void setRuntimeDir(String runtimeDir) {
        this.runtimeDir = runtimeDir;
    }

    public boolean isOpenGui() {
        return openGui;
    }

    public void setOpenGui(boolean openGui) {
        this.openGui = openGui;
    }

    public String getSecrete() {
        return secrete;
    }

    public void setSecrete(String secrete) {
        this.secrete = secrete;
    }

    public String getLoglevel() {
        return loglevel;
    }

    public void setLoglevel(String loglevel) {
        this.loglevel = loglevel;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ClientServiceDescription> getClientServiceDescriptions() {
        return clientServiceDescriptions;
    }

    public void setClientServiceDescriptions(List<ClientServiceDescription> clientServiceDescriptions) {
        this.clientServiceDescriptions = clientServiceDescriptions;
    }

    public Map<String, ClientServiceDescription> getClientServiceDescriptionMap() {
        return clientServiceDescriptionMap;
    }

    public void setClientServiceDescriptionMap(Map<String, ClientServiceDescription> clientServiceDescriptionMap) {
        this.clientServiceDescriptionMap = clientServiceDescriptionMap;
    }

    public InetAddress getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(InetAddress serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public InetSocketAddress getServerIpSocketAddress() {
        return serverIpSocketAddress;
    }

    public void setServerIpSocketAddress(InetSocketAddress serverIpSocketAddress) {
        this.serverIpSocketAddress = serverIpSocketAddress;
    }

    public void loadClientId() {
        String runtimeDir = getRuntimeDir();
        if (!runtimeDir.endsWith(File.separator)) {
            runtimeDir += File.separator;
        }

        String clientIdPath = runtimeDir + "client_id";
        File file = new File(clientIdPath);
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                byte[] clientId = new byte[32];
                fileInputStream = new FileInputStream(file);
                int read = fileInputStream.read(clientId);
                ClientStart.CLIENT_ID = new String(clientId);
            } catch (Exception e) {
                logger.error("load client id fail,cause " + e);
                ApplicationExit.exit();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        logger.error("load client id fail,cause " + e);
                        ApplicationExit.exit();
                    }
                }
            }
        } else {
            ClientStart.CLIENT_ID = UUIDSimple.id();
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(ClientStart.CLIENT_ID.getBytes());
            } catch (Exception e) {
                logger.error("load client id fail,cause " + e);
                ApplicationExit.exit();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        logger.error("load client id fail,cause " + e);
                        ApplicationExit.exit();
                    }
                }
            }

        }

    }
}
