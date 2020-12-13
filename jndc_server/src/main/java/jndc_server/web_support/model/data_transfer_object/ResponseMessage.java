package jndc_server.web_support.model.data_transfer_object;

public class ResponseMessage {
    private int code=200;

    private String message="操作成功";



    public void error(){
        code=500;
    }
    public void error(String message){
      error();
      setMessage(message);
    }

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
