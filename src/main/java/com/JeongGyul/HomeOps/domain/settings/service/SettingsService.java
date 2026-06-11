package com.JeongGyul.HomeOps.domain.settings.service;

import com.JeongGyul.HomeOps.domain.settings.dto.InfraStatusResponse;
import com.JeongGyul.HomeOps.domain.settings.dto.SettingsResponse;
import com.JeongGyul.HomeOps.domain.settings.dto.SettingsUpdateRequest;
import com.JeongGyul.HomeOps.domain.settings.dto.SystemInfoResponse;
import com.JeongGyul.HomeOps.domain.settings.entity.AppSettings;
import com.JeongGyul.HomeOps.domain.settings.repository.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final AppSettingsRepository settingsRepository;
    private final DataSource dataSource;
    private final StringRedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public SettingsResponse getSettings() {
        return settingsRepository.findById(SETTINGS_ID)
                .map(SettingsResponse::from)
                .orElse(SettingsResponse.defaults());
    }

    @Transactional
    public SettingsResponse updateSettings(SettingsUpdateRequest request) {
        AppSettings settings = settingsRepository.findById(SETTINGS_ID)
                .orElseGet(() -> AppSettings.builder().id(SETTINGS_ID).build());
        settings.update(
                request.notifyCrash(),
                request.notifyRecover(),
                request.failThreshold(),
                request.defaultInterval()
        );
        return SettingsResponse.from(settingsRepository.save(settings));
    }

    public InfraStatusResponse getStatus() {
        String mysqlStatus = checkMysql();
        String redisStatus = checkRedis();
        return new InfraStatusResponse(mysqlStatus, redisStatus);
    }

    private String checkMysql() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(1) ? "connected" : "error";
        } catch (Exception e) {
            log.warn("MySQL 연결 확인 실패: {}", e.getMessage());
            return "disconnected";
        }
    }

    private String checkRedis() {
        try (var conn = redisTemplate.getConnectionFactory().getConnection()) {
            return conn.ping() != null ? "connected" : "error";
        } catch (Exception e) {
            log.warn("Redis 연결 확인 실패: {}", e.getMessage());
            return "disconnected";
        }
    }

    public SystemInfoResponse getSystemInfo() {
        String hostname = resolveHostname();
        String localIp  = resolveLocalIp();
        String os       = System.getProperty("os.name") + " " + System.getProperty("os.arch");
        long uptime     = resolveUptimeSeconds();
        return new SystemInfoResponse(hostname, localIp, os, uptime);
    }

    private String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /** 루프백을 제외한 첫 번째 IPv4 주소 반환 */
    private String resolveLocalIp() {
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isLoopback() || !ni.isUp() || ni.isVirtual()) continue;
                for (java.net.InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    InetAddress addr = ia.getAddress();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    /** Linux: /proc/uptime 첫 번째 값(초) / 그 외: JVM 가동 시간 */
    private long resolveUptimeSeconds() {
        Path procUptime = Path.of("/proc/uptime");
        if (Files.exists(procUptime)) {
            try {
                String content = Files.readString(procUptime).trim();
                return (long) Double.parseDouble(content.split("\\s+")[0]);
            } catch (Exception ignored) {}
        }
        // macOS / Windows 개발환경 폴백: JVM 가동 시간
        return ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
    }
}
