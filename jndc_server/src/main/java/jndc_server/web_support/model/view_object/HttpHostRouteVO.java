package jndc_server.web_support.model.view_object;


import lombok.Data;

@Data
public class HttpHostRouteVO {

    private String id;

    private String hostKeyWord;

    private int routeType;//0 do redirect 1 return fixed value

    private String fixedResponse;

    private String redirectAddress;

    private String fixedContentType;

    private String forwardHost;

    private int forwardPort;

    private String forwardProtocol;


}
