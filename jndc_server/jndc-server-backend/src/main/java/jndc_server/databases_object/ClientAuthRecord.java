package jndc_server.databases_object;

import jndc.core.message.DeviceSummary;
import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@DSTable(name = "client_auth_record")
public class ClientAuthRecord {
    @DSKey(name = "client_id")
    private String clientId;

    @DSFiled(name = "client_auth_key")
    private String clientAuthKey;

    @DSFiled(name = "auth_mode")
    private Integer authMode;

    @DSFiled(name = "last_client_ip")
    private String lastClientIp;

    @DSFiled(name = "last_client_port")
    private Integer lastClientPort;

    @DSFiled(name = "last_seen_at")
    private Long lastSeenAt;

    @DSFiled(name = "last_offline_at")
    private Long lastOfflineAt;

    @DSFiled(name = "os_name")
    private String osName;

    @DSFiled(name = "os_version")
    private String osVersion;

    @DSFiled(name = "cpu_model")
    private String cpuModel;

    @DSFiled(name = "cpu_logical_cores")
    private Integer cpuLogicalCores;

    @DSFiled(name = "gpu_names")
    private String gpuNames;

    @DSFiled(name = "memory_total_bytes")
    private Long memoryTotalBytes;

    @DSFiled(name = "disk_total_bytes")
    private Long diskTotalBytes;

    @DSFiled(name = "disk_free_bytes")
    private Long diskFreeBytes;

    @DSFiled(name = "client_to_server_bytes")
    private Long clientToServerBytes;

    @DSFiled(name = "server_to_client_bytes")
    private Long serverToClientBytes;

    public void applyDeviceSummary(DeviceSummary deviceSummary) {
        if (deviceSummary == null) {
            return;
        }
        this.osName = deviceSummary.getOsName();
        this.osVersion = deviceSummary.getOsVersion();
        this.cpuModel = deviceSummary.getCpuModel();
        this.cpuLogicalCores = deviceSummary.getCpuLogicalCores();
        this.gpuNames = joinGpuNames(deviceSummary.getGpuNames());
        this.memoryTotalBytes = deviceSummary.getMemoryTotalBytes();
        this.diskTotalBytes = deviceSummary.getDiskTotalBytes();
        this.diskFreeBytes = deviceSummary.getDiskFreeBytes();
    }

    public DeviceSummary toDeviceSummary() {
        DeviceSummary deviceSummary = new DeviceSummary();
        deviceSummary.setOsName(defaultString(osName));
        deviceSummary.setOsVersion(defaultString(osVersion));
        deviceSummary.setCpuModel(defaultString(cpuModel));
        deviceSummary.setCpuLogicalCores(cpuLogicalCores == null ? 0 : cpuLogicalCores);
        deviceSummary.setGpuNames(parseGpuNames());
        deviceSummary.setMemoryTotalBytes(memoryTotalBytes == null ? 0L : memoryTotalBytes);
        deviceSummary.setDiskTotalBytes(diskTotalBytes == null ? 0L : diskTotalBytes);
        deviceSummary.setDiskFreeBytes(diskFreeBytes == null ? 0L : diskFreeBytes);
        return deviceSummary;
    }

    private List<String> parseGpuNames() {
        if (gpuNames == null || "".equals(gpuNames.trim())) {
            return new ArrayList<>();
        }
        return Arrays.stream(gpuNames.split(","))
                .map(String::trim)
                .filter(value -> !"".equals(value))
                .collect(Collectors.toList());
    }

    private String joinGpuNames(List<String> gpuNames) {
        if (gpuNames == null || gpuNames.isEmpty()) {
            return "";
        }
        return gpuNames.stream()
                .filter(value -> value != null && !"".equals(value.trim()))
                .collect(Collectors.joining(","));
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
