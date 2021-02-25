package jndc_server.web_support.model.view_object;


public class HttpHostRouteVO {

    private String id;

    private String hostKeyWord;

    private int returnFixedValue;//0 do redirect 1 return fixed value

    private String fixedResponse;

    private String redirectAddress;

    private String fixedContentType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFixedContentType() {
        return fixedContentType;
    }

    public void setFixedContentType(String fixedContentType) {
        this.fixedContentType = fixedContentType;
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
