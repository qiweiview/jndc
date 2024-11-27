package com.view.jndc.server.dao.example;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExampleDao {

    List<String> listTables();
}
