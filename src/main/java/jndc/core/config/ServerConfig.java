package jndc.core.config;

import jndc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerConfig  implements ParameterVerification {
    private   final Logger logger = LoggerFactory.getLogger(getClass());


    private String frontProjectPath;

    private int managementApiPort;

    private boolean deployFrontProject;

    private int adminPort;

    private String bindIp;

    private InetAddress inetAddress;

    private InetSocketAddress inetSocketAddress;



    @Override
    public void performParameterVerification() {
        inetAddress = InetUtils.getByStringIpAddress(bindIp);
        inetSocketAddress=new InetSocketAddress(inetAddress,adminPort);
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "adminPort=" + adminPort +
                ", bindIp='" + bindIp + '\'' +
                '}';
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
