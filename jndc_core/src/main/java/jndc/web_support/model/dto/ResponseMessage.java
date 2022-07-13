package jndc.web_support.model.dto;

import lombok.Data;

@Data
public class ResponseMessage {
    private int code = 0;

    private String message = "操作成功";

    private Object data;

    public static ResponseMessage success(Object data) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setData(data);
        return responseMessage;

    }

    public static ResponseMessage fail(String message) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setCode(-1);
        responseMessage.setMessage(message);
        return responseMessage;

    }

    public void error() {
        code = 500;
    }

    public void error(String message) {
        error();
        setMessage(message);
    }

}
