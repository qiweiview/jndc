package com.view.vo.menu;

import lombok.Data;

import java.util.List;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-11 14:48
 * @description: 路由元信息
 */
@Data
public class AsyncRoutesMetaVO {

    private String title;

    private String icon;

    private Boolean showLink;

    private Integer sortOrder;

    private List<String> roles;

    private List<String> auths;

    private Boolean keepAlive;

    private String frameSrc;

    private Boolean frameLoading;



}
