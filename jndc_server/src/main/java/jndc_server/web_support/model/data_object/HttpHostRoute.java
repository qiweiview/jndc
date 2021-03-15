package jndc_server.web_support.model.data_object;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import jndc_server.web_support.model.data_transfer_object.HostRouteDTO;

@DSTable(name = "http_host_route")
public class HttpHostRoute {
    @DSKey
    private String id;

    private String hostKeyWord;

    //0 redirect
    //1 fixed value
    //2 forward
    private int routeType;

    private String fixedResponse;

    private String fixedContentType;

    private String redirectAddress;

    private String forwardHost;

    private int forwardPort;




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
        return httpHostRoute;

    }

    public String getForwardHost() {
        return forwardHost;
    }

    public void setForwardHost(String forwardHost) {
        this.forwardHost = forwardHost;
    }

    public int getForwardPort() {
        return forwardPort;
    }

    public void setForwardPort(int forwardPort) {
        this.forwardPort = forwardPort;
    }

    public String getFixedContentType() {
        return fixedContentType;
    }

    public void setFixedContentType(String fixedContentType) {
        this.fixedContentType = fixedContentType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostKeyWord() {
        return hostKeyWord;
    }

    public void setHostKeyWord(String hostKeyWord) {
        this.hostKeyWord = hostKeyWord;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public String getFixedResponse() {
        return fixedResponse;
    }

    public void setFixedResponse(String fixedResponse) {
        this.fixedResponse = fixedResponse;
    }

    public String getRedirectAddress() {
        return redirectAddress;
    }

    public void setRedirectAddress(String redirectAddress) {
        this.redirectAddress = redirectAddress;
    }
}
