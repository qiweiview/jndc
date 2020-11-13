package jndc.utils;


import jndc.core.TcpServiceDescription;
import jndc.core.message.RegistrationMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public  class ObjectSerializableUtils implements Serializable {


    public static byte[] object2bytes(Object obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T>T bytes2object(byte[] data, Class<T> tClass) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(in);
            Object o = is.readObject();
            return (T) o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }






}