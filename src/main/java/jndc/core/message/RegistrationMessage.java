package jndc.core.message;

import java.io.Serializable;

/**
 * 请求响应共体
 */
public class RegistrationMessage implements Serializable {


    private static final long serialVersionUID = 2323315614144754699L;

    /* -------------------请求------------------- */
    private String equipmentId;


    /* -------------------响应------------------- */
    private String message;




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
