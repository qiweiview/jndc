package jndc.core.config;

import jndc.core.IpChecker;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store.DBWrapper;
import jndc.server.IpFilterRule4V;
import jndc.utils.InetUtils;
import jndc.utils.UUIDSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Stream;

public class ServerConfig  implements ParameterVerification {
    private   final Logger logger = LoggerFactory.getLogger(getClass());


    private String frontProjectPath;

    private int managementApiPort;

    private boolean deployFrontProject;

    private int adminPort;

    private String bindIp;

    private InetAddress inetAddress;

    private InetSocketAddress inetSocketAddress;

    private String[] blackList;

    private String[] whiteList;





    @Override
    public void performParameterVerification() {
        inetAddress = InetUtils.getByStringIpAddress(bindIp);
        inetSocketAddress=new InetSocketAddress(inetAddress,adminPort);
    }

    @Override
    public void lazyInitAfterVerification() {


        DBWrapper<IpFilterRule4V> dbWrapper = DBWrapper.getDBWrapper(IpFilterRule4V.class);
        List<IpFilterRule4V> ipFilterRule4VS = dbWrapper.listAll();
        Map<String, IpFilterRule4V>  blackMap=new HashMap<>();
        Map<String, IpFilterRule4V>  whiteMap=new HashMap<>();
        ipFilterRule4VS.forEach(x->{
            if (x.isBlack()){
                blackMap.put(x.getIp(),x);
            }else {
                whiteMap.put(x.getIp(),x);
            }
        });



        IpChecker ipChecker = UniqueBeanManage.getBean(IpChecker.class);
        if (blackList==null){
            blackList=new String[0];
        }

        if (whiteList==null){
            whiteList=new String[0];
        }

        List<IpFilterRule4V> storeList=new ArrayList<>();

        Stream.of(blackList).forEach(x->{
            if (!blackMap.containsKey(x)){

                IpFilterRule4V ipFilterRule4V = new IpFilterRule4V();
                ipFilterRule4V.black();
                ipFilterRule4V.setId(UUIDSimple.id());
                ipFilterRule4V.setIp(x);
                blackMap.put(x,ipFilterRule4V);
                storeList.add(ipFilterRule4V);
            }
        });

        Stream.of(whiteList).forEach(x->{
            if (!whiteMap.containsKey(x)){
                IpFilterRule4V ipFilterRule4V = new IpFilterRule4V();
                ipFilterRule4V.white();
                ipFilterRule4V.setId(UUIDSimple.id());
                ipFilterRule4V.setIp(x);
                whiteMap.put(x,ipFilterRule4V);
                storeList.add(ipFilterRule4V);
            }
        });

        if (storeList.size()>0){
            dbWrapper.insertBatch(storeList);
            logger.info("add new ip filter rule:"+storeList);
        }

        ipChecker.loadRule(blackMap,whiteMap);

    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "adminPort=" + adminPort +
                ", bindIp='" + bindIp + '\'' +
                '}';
    }
    public String[] getBlackList() {
        return blackList;
    }

    public void setBlackList(String[] blackList) {
        this.blackList = blackList;
    }

    public String[] getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String[] whiteList) {
        this.whiteList = whiteList;
    }


    public String getFrontProjectPath() {
        return frontProjectPath;
    }

    public void setFrontProjectPath(String frontProjectPath) {
        this.frontProjectPath = frontProjectPath;
    }

    public boolean isDeployFrontProject() {
        return deployFrontProject;
    }

    public void setDeployFrontProject(boolean deployFrontProject) {
        this.deployFrontProject = deployFrontProject;
    }

    public int getManagementApiPort() {
        return managementApiPort;
    }

    public void setManagementApiPort(int managementApiPort) {
        this.managementApiPort = managementApiPort;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    public Logger getLogger() {
        return logger;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

    public String getBindIp() {
        return bindIp;
    }

    public void setBindIp(String bindIp) {
        this.bindIp = bindIp;
    }


}
