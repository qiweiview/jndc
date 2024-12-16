package com.view.vo.menu;

import lombok.Data;

import java.util.List;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-11 14:46
 * @description: 动态路由
 */
@Data
public class AsyncRoutesVO {

    private String path;

    private String name;

    private String redirect;

    private String component;

    private Integer type;

    private AsyncRoutesMetaVO meta;

    private List<AsyncRoutesVO> children;
}
