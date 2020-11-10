package web.model.data_transfer_object;

public class ResponseMessage {
    private int code=200;

    private String message="操作成功";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
