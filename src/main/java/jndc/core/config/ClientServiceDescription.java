package jndc.core.config;


import jndc.core.TcpServiceDescription;
import jndc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * client service capability description
 */
public class ClientServiceDescription implements ParameterVerification {
    private   final Logger logger = LoggerFactory.getLogger(getClass());


    private String serviceName;

    private String serviceIp;

    private int servicePort;

    private boolean serviceEnable;


    /* ---------prepare  file-----*/
    private InetAddress ipAddress;

    private InetSocketAddress ipSocketAddress;




    @Override
    public void performParameterVerification() {
        ipAddress = InetUtils.getByStringIpAddress(serviceIp);
        ipSocketAddress =new InetSocketAddress(ipAddress, servicePort);
    }

    @Override
    public void lazyInitAfterVerification() {

    }

    public String getUniqueTag(){
        return serviceIp+":"+servicePort;
    }

    /* -------getter setter-------- */

    public Logger getLogger() {
        return logger;
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
