package com.JeongGyul.HomeOps.domain.monitoring.service;

import com.JeongGyul.HomeOps.domain.monitoring.dto.ResourceResponse;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Slf4j
@Service
public class ResourceCollectorService {

    private static final Path TEMP_SENSOR = Path.of("/sys/class/thermal/thermal_zone0/temp");

    private long prevRxBytes = 0;
    private long prevTxBytes = 0;
    private long prevNetTime = System.currentTimeMillis();

    public ResourceResponse collect() {
        OperatingSystemMXBean os =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        double cpu = Math.max(0.0, os.getCpuLoad() * 100);
        double ram = calcRamUsage(os);
        double temp = readTemperature(cpu);
        double network = calcNetworkMbps();

        return new ResourceResponse(
                Math.round(cpu * 10.0) / 10.0,
                Math.round(ram * 10.0) / 10.0,
                Math.round(temp * 10.0) / 10.0,
                Math.round(network * 100.0) / 100.0
        );
    }

    private double calcRamUsage(OperatingSystemMXBean os) {
        long total = os.getTotalMemorySize();
        if (total <= 0) return 0.0;

        // Linux(라즈베리파이): MemAvailable 사용 — 캐시 반환분 포함 실제 사용 가능 메모리
        Path memInfo = Path.of("/proc/meminfo");
        if (Files.exists(memInfo)) {
            try {
                for (String line : Files.readAllLines(memInfo)) {
                    if (line.startsWith("MemAvailable:")) {
                        long availableKb = Long.parseLong(line.split("\\s+")[1]);
                        long available = availableKb * 1024L;
                        return (double) (total - available) / total * 100;
                    }
                }
            } catch (Exception ignored) {}
        }

        // macOS 개발환경: vm_stat 파싱 — page size × (free + inactive) = 실제 사용 가능
        try {
            Process p = Runtime.getRuntime().exec("vm_stat");
            String output = new String(p.getInputStream().readAllBytes());
            long pageSize = 4096L;
            long freePages = 0, inactivePages = 0;
            for (String line : output.split("\n")) {
                if (line.startsWith("Mach Virtual Memory Statistics")) {
                    // "page size of X bytes" 추출
                    java.util.regex.Matcher m = java.util.regex.Pattern
                            .compile("page size of (\\d+) bytes").matcher(line);
                    if (m.find()) pageSize = Long.parseLong(m.group(1));
                } else if (line.startsWith("Pages free:")) {
                    freePages = Long.parseLong(line.replaceAll("[^0-9]", ""));
                } else if (line.startsWith("Pages inactive:")) {
                    inactivePages = Long.parseLong(line.replaceAll("[^0-9]", ""));
                }
            }
            long available = (freePages + inactivePages) * pageSize;
            if (available > 0 && available <= total) {
                return (double) (total - available) / total * 100;
            }
        } catch (Exception ignored) {}

        // 최후 폴백: JVM 기본 API (macOS에서 부정확할 수 있음)
        long free = os.getFreeMemorySize();
        return (double) (total - free) / total * 100;
    }

    private double readTemperature(double cpuLoad) {
        if (Files.exists(TEMP_SENSOR)) {
            try {
                int milliCelsius = Integer.parseInt(Files.readString(TEMP_SENSOR).trim());
                return milliCelsius / 1000.0;
            } catch (Exception ignored) {
            }
        }
        // Mac/Windows 개발환경: CPU 부하 기반 추정
        return 35.0 + (cpuLoad * 0.4);
    }

    private synchronized double calcNetworkMbps() {
        long rxBytes = 0;
        long txBytes = 0;
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isLoopback() || !ni.isUp()) continue;
                // 바이트 카운터 직접 접근은 OS마다 다르므로 /proc/net/dev 사용 (Linux)
            }
            // Linux: /proc/net/dev 파싱
            Path procNet = Path.of("/proc/net/dev");
            if (Files.exists(procNet)) {
                for (String line : Files.readAllLines(procNet)) {
                    if (line.contains(":") && !line.contains("lo:")) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length >= 10) {
                            rxBytes += Long.parseLong(parts[1]);
                            txBytes += Long.parseLong(parts[9]);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        long now = System.currentTimeMillis();
        long elapsed = Math.max(1, now - prevNetTime);
        long deltaBytes = (rxBytes + txBytes) - (prevRxBytes + prevTxBytes);
        double mbps = (double) deltaBytes / elapsed * 1000 / (1024 * 1024);

        prevRxBytes = rxBytes;
        prevTxBytes = txBytes;
        prevNetTime = now;

        return Math.max(0.0, mbps);
    }
}
