package jndc_server.web_support.model.data_transfer_object;

public class HostRouteDTO {
    private String id;

    private String hostKeyWord;

    private int returnFixedValue;//0 do redirect 1 return fixed value

    private String fixedResponse;

    private String fixedContentType;

    private String redirectAddress;

    private int page;

    private int rows;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
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
