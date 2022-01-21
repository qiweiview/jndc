package jndc_server.web_support.model.data_object;

import lombok.Data;

@Data
public class ManagementLoginUser {
    private String name;

    private String passWord;


    @Override
    public String toString() {
        return "ManagementLoginUser{" +
                "name='" + name + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }

}
