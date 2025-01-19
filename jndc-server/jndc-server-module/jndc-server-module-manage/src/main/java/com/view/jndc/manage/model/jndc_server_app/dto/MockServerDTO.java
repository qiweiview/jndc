package com.view.jndc.manage.model.jndc_server_app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

//通过注解允许jackson反序列化属性为空
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MockServerDTO {
    private String contentType;

    private String mockData;

    private Boolean useSSL;
}
