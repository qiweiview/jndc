package jndc.core.message;

import jndc.utils.ObjectSerializableUtils;

import java.io.Serializable;

public class SerializableMessage implements Serializable {

    private static final long serialVersionUID = -8054946036275082060L;

    public byte[] object2Bytes(){
        return ObjectSerializableUtils.object2bytes(this);
    }


}
