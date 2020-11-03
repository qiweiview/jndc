package jndc.core.message;

import java.io.Serializable;

/**
 * 请求响应共体
 */
public class RegistrationMessage implements Serializable {


    private static final long serialVersionUID = 2323315614144754699L;


    private String auth;

    private String equipmentId;

    private String message;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Override
    public String toString() {
        return "RegistrationMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
