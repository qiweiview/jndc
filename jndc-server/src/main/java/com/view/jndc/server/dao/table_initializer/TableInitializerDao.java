package com.view.jndc.server.dao.table_initializer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface TableInitializerDao {


    int execute(@Param("sql") String sql);
}
