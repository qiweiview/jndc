package device

import (
	"bufio"
	"bytes"
	"log/slog"
	"os"
	"os/exec"
	"path/filepath"
	stdruntime "runtime"
	"strconv"
	"strings"
	"syscall"

	"jndc-client-go/internal/protocol"
)

func Collect(logger *slog.Logger, diskPath string) protocol.DeviceSummary {
	summary := protocol.DeviceSummary{
		OSName:           stdruntime.GOOS,
		OSVersion:        safe(commandOutput("uname", "-r")),
		CPUModel:         cpuModel(),
		CPULogicalCores:  stdruntime.NumCPU(),
		GPUNames:         []string{},
		MemoryTotalBytes: memoryTotalBytes(),
	}

	total, free := diskBytes(diskPath)
	summary.DiskTotalBytes = total
	summary.DiskFreeBytes = free

	if logger != nil {
		logger.Debug("device summary collected", "os", summary.OSName, "cores", summary.CPULogicalCores)
	}
	return summary
}

func cpuModel() string {
	switch stdruntime.GOOS {
	case "darwin":
		return safe(commandOutput("sysctl", "-n", "machdep.cpu.brand_string"))
	case "linux":
		file, err := os.Open("/proc/cpuinfo")
		if err != nil {
			return ""
		}
		defer file.Close()
		scanner := bufio.NewScanner(file)
		for scanner.Scan() {
			line := scanner.Text()
			if strings.HasPrefix(line, "model name") {
				parts := strings.SplitN(line, ":", 2)
				if len(parts) == 2 {
					return strings.TrimSpace(parts[1])
				}
			}
		}
	}
	return ""
}

func memoryTotalBytes() int64 {
	switch stdruntime.GOOS {
	case "darwin":
		value := safe(commandOutput("sysctl", "-n", "hw.memsize"))
		n, _ := strconv.ParseInt(value, 10, 64)
		return n
	case "linux":
		file, err := os.Open("/proc/meminfo")
		if err != nil {
			return 0
		}
		defer file.Close()
		scanner := bufio.NewScanner(file)
		for scanner.Scan() {
			line := scanner.Text()
			if strings.HasPrefix(line, "MemTotal:") {
				fields := strings.Fields(line)
				if len(fields) >= 2 {
					value, _ := strconv.ParseInt(fields[1], 10, 64)
					return value * 1024
				}
			}
		}
	}
	return 0
}

func diskBytes(path string) (int64, int64) {
	target := path
	if target == "" {
		target = "."
	}
	target, _ = filepath.Abs(target)
	var stat syscall.Statfs_t
	if err := syscall.Statfs(target, &stat); err != nil {
		return 0, 0
	}
	total := int64(stat.Blocks) * int64(stat.Bsize)
	free := int64(stat.Bavail) * int64(stat.Bsize)
	return total, free
}

func commandOutput(name string, args ...string) string {
	cmd := exec.Command(name, args...)
	output, err := cmd.Output()
	if err != nil {
		return ""
	}
	return strings.TrimSpace(string(bytes.TrimSpace(output)))
}

func safe(value string) string {
	if value == "" {
		return ""
	}
	return value
}
