package jndc_client.core;


import jndc.core.message.TcpServiceDescription;
import jndc.utils.InetUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * client service capability description
 */
@Slf4j
public class ClientServiceDescription {


    private String serviceName;

    private String serviceIp;

    private int servicePort;

    private boolean serviceEnable;


    /* ---------prepare  field-----*/
    private InetAddress ipAddress;

    private InetSocketAddress ipSocketAddress;


    /**
     * 参数校验
     */
    public void performParameterVerification() {
        ipAddress = InetUtils.getByStringIpAddress(serviceIp);
        ipSocketAddress = new InetSocketAddress(ipAddress, servicePort);
    }


    @Override
    public String toString() {
        return "ClientServiceDescription{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceIp='" + serviceIp + '\'' +
                ", servicePort=" + servicePort +
                ", serviceEnable=" + serviceEnable +
                '}';
    }

    public String getUniqueTag() {
        return serviceIp + ":" + servicePort;
    }

    /* -------getter setter-------- */

    public Logger getLogger() {
        return log;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public boolean isServiceEnable() {
        return serviceEnable;
    }

    public void setServiceEnable(boolean serviceEnable) {
        this.serviceEnable = serviceEnable;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public InetSocketAddress getIpSocketAddress() {
        return ipSocketAddress;
    }

    public void setIpSocketAddress(InetSocketAddress ipSocketAddress) {
        this.ipSocketAddress = ipSocketAddress;
    }

    public TcpServiceDescription toTcpServiceDescription() {
        TcpServiceDescription tcpServiceDescription = new TcpServiceDescription();
        tcpServiceDescription.setPort(servicePort);
        tcpServiceDescription.setIp(serviceIp);
        tcpServiceDescription.setName(serviceName);
        return tcpServiceDescription;
    }
}
