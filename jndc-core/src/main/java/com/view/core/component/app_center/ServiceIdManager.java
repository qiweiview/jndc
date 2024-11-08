package com.view.core.component.app_center;

import com.view.core.utils.RuntimeUtils;
import com.view.core.utils.UniqueId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServiceIdManager {
    private Map<String, String> serviceIdMap = new HashMap<>();

    public static final String SERVICE_META_FILE_NAME = "service_meta.csv";

    public static final String ABSOLUTE_PATH = RuntimeUtils.NDC_RUNTIME_DIR + File.separator + SERVICE_META_FILE_NAME;

    public ServiceIdManager() {
        serviceMetaFileCheck();
        loadDataFromFile();
    }

    /**
     * 加载服务元数据文件
     */
    public void loadDataFromFile() {
        //todo 加载服务元数据文件

        try {
            List<String> lines = Files.readAllLines(Paths.get(ABSOLUTE_PATH));
            lines.forEach(line -> {
                String[] split = line.split(",");
                serviceIdMap.put(split[0], split[1]);
            });
        } catch (IOException e) {
            log.error("加载服务元数据文件失败", e);
        }

    }

    /**
     * 服务元数据文件检查
     */
    public void serviceMetaFileCheck() {
        //todo 服务元数据文件检查
        File file = new File(RuntimeUtils.NDC_RUNTIME_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }


        File serviceMetaFile = new File(ABSOLUTE_PATH);
        if (!serviceMetaFile.exists()) {
            try {
                serviceMetaFile.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("创建服务元数据文件失败", e);
            }
        }

    }

    public String generateServiceId(String host, int port) {
        String serviceIdMeta = host + ":" + port;
        String serviceId = serviceIdMap.get(serviceIdMeta);
        if (serviceId == null) {
            serviceId = UniqueId.generate();
            serviceIdMap.put(serviceIdMeta, serviceId);
            flushToFile();
        }
        return serviceId;
    }

    private void flushToFile() {
        //确认文件存在
        serviceMetaFileCheck();
        List<String> linesToWrite = new ArrayList<>();
        serviceIdMap.forEach((k, v) -> {
            linesToWrite.add(k + "," + v);
        });

        try {
            // 使用Files.write覆盖写入文件
            Files.write(Paths.get(ABSOLUTE_PATH), linesToWrite);
            log.info("写入服务元数据文件成功");
        } catch (IOException e) {
            // 异常处理
            log.error("写入服务元数据文件失败", e);
        }
    }
}
