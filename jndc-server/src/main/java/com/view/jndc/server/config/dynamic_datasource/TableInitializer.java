package com.view.jndc.server.config.dynamic_datasource;

import com.view.core.component.general_control.plugins.ip_blocker.IPRecord;
import com.view.core.component.general_control.plugins.time_blocker.TimeRange;
import com.view.core.model.ChannelOpen;
import com.view.jndc.server.dao.table_initializer.TableInitializerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableInitializer {
    private final TableInitializerDao tableInitializerDao;


    public void init() {
        tableInitializerDao.execute(IPRecord.ddl());
        tableInitializerDao.execute(TimeRange.ddl());
        tableInitializerDao.execute(ChannelOpen.ddl());
    }
}
