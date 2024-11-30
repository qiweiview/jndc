package com.view.jndc.server.config.dynamic_datasource;


import com.view.jndc.server.dao.table_initializer.TableInitializerDao;
import com.view.jndc.server.model.admin.PureMetaEntity;
import com.view.jndc.server.model.admin.PurePermissionEntity;
import com.view.jndc.server.model.admin.PureRouteEntity;
import com.view.jndc.server.model.admin.PureUserEntity;
import com.view.jndc.server.model.ndc.plugins.IPRecordEntity;
import com.view.jndc.server.model.ndc.plugins.TimeRangeEntity;
import com.view.jndc.server.model.ndc.server.ChannelOpenEntity;
import com.view.jndc.server.model.ndc.server.NDCServerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableInitializer {
    private final TableInitializerDao tableInitializerDao;


    public void init() {
        tableInitializerDao.execute(IPRecordEntity.ddl());
        tableInitializerDao.execute(TimeRangeEntity.ddl());
        tableInitializerDao.execute(ChannelOpenEntity.ddl());
        tableInitializerDao.execute(NDCServerEntity.ddl());


        tableInitializerDao.execute(PureUserEntity.ddl());
        tableInitializerDao.execute(PureMetaEntity.ddl());
        tableInitializerDao.execute(PurePermissionEntity.ddl());
        tableInitializerDao.execute(PureRouteEntity.ddl());
    }
}
