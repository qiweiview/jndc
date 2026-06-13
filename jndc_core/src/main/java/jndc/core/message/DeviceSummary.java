package jndc.core.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceSummary implements Serializable {
    private static final long serialVersionUID = 2541675352198332377L;

    private String osName;

    private String osVersion;

    private String cpuModel;

    private int cpuLogicalCores;

    private List<String> gpuNames;

    private long memoryTotalBytes;

    private long diskTotalBytes;

    private long diskFreeBytes;
}
