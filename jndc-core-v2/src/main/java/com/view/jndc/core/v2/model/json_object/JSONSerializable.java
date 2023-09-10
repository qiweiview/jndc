package com.view.jndc.core.v2.model.json_object;

import com.view.jndc.core.v2.enum_value.ResponseCode;
import com.view.jndc.core.v2.utils.JacksonObjectMapper;
import lombok.Data;

@Data
public class JSONSerializable {

    /**
     * 响应编号
     */
    public Integer code;

    /**
     * 响应文本
     */
    public String msg;


    public boolean successCheck() {
        return ResponseCode.SUCCESS.value.equals(code);
    }


    public void success(String msg) {
        this.code = ResponseCode.SUCCESS.value;
        this.msg = msg;

    }

    public void fail(String msg) {
        this.code = ResponseCode.FAIL.value;
        this.msg = msg;
    }


    public byte[] serialize() {
        return JacksonObjectMapper.serialize2ByteArray(this);

    }


    public static <T> T deserialize(byte[] data, Class<T> tClass) {
        return JacksonObjectMapper.deserialize(new String(data), tClass);
    }
}
