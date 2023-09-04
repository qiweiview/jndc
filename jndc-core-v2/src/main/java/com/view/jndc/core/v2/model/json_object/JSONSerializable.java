package com.view.jndc.core.v2.model.json_object;

import com.view.jndc.core.v2.utils.JacksonObjectMapper;

public class JSONSerializable {


    public byte[] serialize() {
        return JacksonObjectMapper.serialize2ByteArray(this);

    }


    public <T> T deserialize(byte[] data, Class<T> tClass) {
        return JacksonObjectMapper.deserialize(new String(data), tClass);
    }
}
