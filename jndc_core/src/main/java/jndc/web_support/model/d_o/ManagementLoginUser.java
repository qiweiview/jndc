package jndc.web_support.model.d_o;

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
