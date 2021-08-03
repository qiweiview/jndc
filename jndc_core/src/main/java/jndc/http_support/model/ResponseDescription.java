package jndc.http_support.model;

import jndc.http_support.WebMapping;
import lombok.Data;

@Data
public class ResponseDescription {
    private byte[] data;

    private WebMapping.RESPONSE_TYPE responseType = WebMapping.RESPONSE_TYPE.JSON;
}
