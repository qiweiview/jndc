package jndc_client.core;


import jndc.core.message.TcpServiceDescription;
import jndc.utils.InetUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * 客户端服务
 */
@Slf4j
@Data
public class ClientServiceDescription extends TcpServiceDescription {

    private boolean serviceEnable;

    /* ---------prepare  field-----*/
    private InetAddress ipAddress;

    private InetSocketAddress ipSocketAddress;


    /**
     * 获取唯一标志位
     *
     * @return
     */
    public String getUniqueTag() {
        return getServiceIp() + ":" + getServicePort();
    }

    /**
     * 参数校验
     */
    public void performParameterVerification() {
        ipAddress = InetUtils.getByStringIpAddress(getServiceIp());
        ipSocketAddress = new InetSocketAddress(ipAddress, getServicePort());
    }


    public TcpServiceDescription toTcpServiceDescription() {
        TcpServiceDescription tcpServiceDescription = new TcpServiceDescription();
        tcpServiceDescription.setId(getId());
        tcpServiceDescription.setServiceIp(getServiceIp());
        tcpServiceDescription.setServiceName(getServiceName());
        tcpServiceDescription.setServicePort(getServicePort());
        tcpServiceDescription.setDescription(getServiceName());
        return tcpServiceDescription;
    }
}
