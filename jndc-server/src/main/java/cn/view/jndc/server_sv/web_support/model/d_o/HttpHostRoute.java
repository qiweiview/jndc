package cn.view.jndc.server_sv.web_support.model.d_o;


import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import jndc_server.web_support.model.dto.HostRouteDTO;
import lombok.Data;

@Data
@DSTable(name = "http_host_route")
public class HttpHostRoute {
    @DSKey
    private String id;

    @DSFiled(name = "host_key_word")
    private String hostKeyWord;

    //0 redirect
    //1 fixed value
    //2 forward
    @DSFiled(name = "route_type")
    private int routeType;

    @DSFiled(name = "fixed_response")
    private String fixedResponse;

    @DSFiled(name = "fixed_content_type")
    private String fixedContentType;

    @DSFiled(name = "redirect_address")
    private String redirectAddress;

    @DSFiled(name = "forward_host")
    private String forwardHost;

    @DSFiled(name = "forward_port")
    private int forwardPort;

    @DSFiled(name = "forward_protocol")
    private String forwardProtocol;

    public String getForwardProtocol() {
        return forwardProtocol;
    }

    public void setForwardProtocol(String forwardProtocol) {
        this.forwardProtocol = forwardProtocol;
    }


    public boolean fixValueType(){
        //todo 1
        return routeType ==1;
    }

    public boolean redirectType(){
        //todo 0
        return routeType ==0;
    }

    public boolean forwardType(){
        //todo 2
        return routeType ==2;
    }

    public static HttpHostRoute of(HostRouteDTO hostRouteDTO) {
        HttpHostRoute httpHostRoute = new HttpHostRoute();
        httpHostRoute.setFixedContentType(hostRouteDTO.getFixedContentType());
        httpHostRoute.setFixedResponse(hostRouteDTO.getFixedResponse());
        httpHostRoute.setHostKeyWord(hostRouteDTO.getHostKeyWord());
        httpHostRoute.setRedirectAddress(hostRouteDTO.getRedirectAddress());
        httpHostRoute.setRouteType(hostRouteDTO.getRouteType());
        httpHostRoute.setForwardHost(hostRouteDTO.getForwardHost());
        httpHostRoute.setForwardPort(hostRouteDTO.getForwardPort());
        httpHostRoute.setForwardProtocol(hostRouteDTO.getForwardProtocol());
        return httpHostRoute;

    }

}
