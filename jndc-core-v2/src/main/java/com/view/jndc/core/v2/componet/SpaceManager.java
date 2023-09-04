package com.view.jndc.core.v2.componet;

import com.view.jndc.core.v2.constant.os.WorkDirectName;
import com.view.jndc.core.v2.utils.PathUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Data
@Slf4j
public class SpaceManager {


    public String config;

    public String temp;

    public String download;

    public void printRuntime() {
        String runTimePath = PathUtils.getRuntimeDir();
        log.info("程序运行目录：" + runTimePath);
    }

    public void createWorkDirect() {
        String runTimePath = PathUtils.getRuntimeDir();
        config = runTimePath + File.separator + WorkDirectName.DIR_CONFIG;
        mkdir(config);
        temp = runTimePath + File.separator + WorkDirectName.DIR_TEMP;
        mkdir(temp);
        download = runTimePath + File.separator + WorkDirectName.DIR_DOWNLOAD;
        mkdir(download);
    }

    private void mkdir(String path) {
        File file = new File(path);
        String absolutePath = file.getAbsolutePath();

        if (!file.exists()) {
            log.info(absolutePath + "不存在，执行创建");
            file.mkdirs();
        }
    }
}
