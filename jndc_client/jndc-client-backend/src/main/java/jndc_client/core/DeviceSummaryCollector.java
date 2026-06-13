package jndc_client.core;

import jndc.core.message.DeviceSummary;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class DeviceSummaryCollector {

    private DeviceSummaryCollector() {
    }

    public static DeviceSummary collect() {
        DeviceSummary deviceSummary = new DeviceSummary();
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            collectOperatingSystem(deviceSummary, systemInfo.getOperatingSystem());
            collectProcessor(deviceSummary, hardware.getProcessor());
            collectGraphics(deviceSummary, hardware.getGraphicsCards());
            collectMemory(deviceSummary, hardware.getMemory());
            collectDisk(deviceSummary, systemInfo.getOperatingSystem().getFileSystem());
        } catch (RuntimeException e) {
            log.warn("采集设备摘要失败，降级为空摘要: {}", e.getMessage());
        }
        return deviceSummary;
    }

    private static void collectOperatingSystem(DeviceSummary deviceSummary, OperatingSystem operatingSystem) {
        if (operatingSystem == null) {
            return;
        }
        deviceSummary.setOsName(safeString(operatingSystem.getFamily()));
        OperatingSystem.OSVersionInfo versionInfo = operatingSystem.getVersionInfo();
        if (versionInfo != null) {
            deviceSummary.setOsVersion(safeString(versionInfo.getVersion()));
        }
    }

    private static void collectProcessor(DeviceSummary deviceSummary, CentralProcessor processor) {
        if (processor == null) {
            return;
        }
        CentralProcessor.ProcessorIdentifier identifier = processor.getProcessorIdentifier();
        if (identifier != null) {
            deviceSummary.setCpuModel(safeString(identifier.getName()));
        }
        deviceSummary.setCpuLogicalCores(processor.getLogicalProcessorCount());
    }

    private static void collectGraphics(DeviceSummary deviceSummary, List<GraphicsCard> graphicsCards) {
        if (graphicsCards == null || graphicsCards.isEmpty()) {
            return;
        }
        List<String> gpuNames = new ArrayList<>();
        graphicsCards.forEach(card -> {
            if (card != null) {
                String name = safeString(card.getName());
                if (!"".equals(name)) {
                    gpuNames.add(name);
                }
            }
        });
        deviceSummary.setGpuNames(gpuNames);
    }

    private static void collectMemory(DeviceSummary deviceSummary, GlobalMemory memory) {
        if (memory == null) {
            return;
        }
        deviceSummary.setMemoryTotalBytes(memory.getTotal());
    }

    private static void collectDisk(DeviceSummary deviceSummary, FileSystem fileSystem) {
        if (fileSystem == null) {
            return;
        }
        long total = 0L;
        long free = 0L;
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        if (fileStores == null) {
            return;
        }
        for (OSFileStore fileStore : fileStores) {
            if (fileStore == null) {
                continue;
            }
            total += Math.max(fileStore.getTotalSpace(), 0L);
            free += Math.max(fileStore.getUsableSpace(), 0L);
        }
        deviceSummary.setDiskTotalBytes(total);
        deviceSummary.setDiskFreeBytes(free);
    }

    private static String safeString(String value) {
        return value == null ? "" : value;
    }
}
