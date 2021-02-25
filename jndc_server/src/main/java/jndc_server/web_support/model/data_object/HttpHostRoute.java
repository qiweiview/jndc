package jndc_server.web_support.model.data_object;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import jndc_server.web_support.model.data_transfer_object.HostRouteDTO;

@DSTable(name = "http_host_route")
public class HttpHostRoute {
    @DSKey
    private String id;

    private String hostKeyWord;

    private int returnFixedValue;//0 do redirect 1 return fixed value

    private String fixedResponse;

    private String redirectAddress;

    private String fixedContentType;


    public boolean returnFixedValue(){
        return returnFixedValue==1;
    }

    public static HttpHostRoute of(HostRouteDTO hostRouteDTO) {
        HttpHostRoute httpHostRoute = new HttpHostRoute();
        httpHostRoute.setFixedContentType(hostRouteDTO.getFixedContentType());
        httpHostRoute.setFixedResponse(hostRouteDTO.getFixedResponse());
        httpHostRoute.setHostKeyWord(hostRouteDTO.getHostKeyWord());
        httpHostRoute.setRedirectAddress(hostRouteDTO.getRedirectAddress());
        httpHostRoute.setReturnFixedValue(hostRouteDTO.getReturnFixedValue());
        return httpHostRoute;

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

    public int getReturnFixedValue() {
        return returnFixedValue;
    }

    public void setReturnFixedValue(int returnFixedValue) {
        this.returnFixedValue = returnFixedValue;
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
