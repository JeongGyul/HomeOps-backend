package com.JeongGyul.HomeOps.domain.monitoring.service;

import com.JeongGyul.HomeOps.domain.monitoring.entity.MonitoredService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

@Slf4j
@Service
public class HealthCheckService {

    private static final int TIMEOUT_MS = 5000;

    public HealthCheckResult check(MonitoredService service) {
        return switch (service.getCheckType()) {
            case HTTP    -> checkHttp(service.getTarget());
            case TCP     -> checkTcp(service.getTarget());
            case PROCESS -> checkProcess(service.getTarget());
        };
    }

    private HealthCheckResult checkHttp(String url) {
        long start = System.currentTimeMillis();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestMethod("GET");
            int status = conn.getResponseCode();
            long latency = System.currentTimeMillis() - start;
            return new HealthCheckResult(status < 500, latency);
        } catch (IOException e) {
            log.debug("HTTP 체크 실패: {} - {}", url, e.getMessage());
            return HealthCheckResult.down();
        }
    }

    private HealthCheckResult checkTcp(String target) {
        // "host:port" 형식
        String[] parts = target.split(":");
        if (parts.length != 2) {
            log.warn("잘못된 TCP 대상 형식: {}", target);
            return HealthCheckResult.down();
        }
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(parts[0], Integer.parseInt(parts[1])), TIMEOUT_MS);
            return new HealthCheckResult(true, System.currentTimeMillis() - start);
        } catch (IOException e) {
            log.debug("TCP 체크 실패: {} - {}", target, e.getMessage());
            return HealthCheckResult.down();
        }
    }

    private HealthCheckResult checkProcess(String processName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("pgrep", "-f", processName);
            Process process = pb.start();
            boolean running = process.waitFor() == 0;
            return new HealthCheckResult(running, null);
        } catch (IOException | InterruptedException e) {
            log.debug("PROCESS 체크 실패: {} - {}", processName, e.getMessage());
            Thread.currentThread().interrupt();
            return HealthCheckResult.down();
        }
    }
}
