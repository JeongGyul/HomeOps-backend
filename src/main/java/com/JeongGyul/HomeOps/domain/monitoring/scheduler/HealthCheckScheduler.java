package com.JeongGyul.HomeOps.domain.monitoring.scheduler;

import com.JeongGyul.HomeOps.domain.monitoring.entity.HeathCheckLog;
import com.JeongGyul.HomeOps.domain.monitoring.entity.MonitoredService;
import com.JeongGyul.HomeOps.domain.monitoring.enums.EventType;
import com.JeongGyul.HomeOps.domain.monitoring.event.ServiceCrashedEvent;
import com.JeongGyul.HomeOps.domain.monitoring.event.ServiceRecoveredEvent;
import com.JeongGyul.HomeOps.domain.monitoring.repository.HealthCheckLogRepository;
import com.JeongGyul.HomeOps.domain.monitoring.repository.MonitoredServiceRepository;
import com.JeongGyul.HomeOps.domain.monitoring.service.HealthCheckResult;
import com.JeongGyul.HomeOps.domain.monitoring.service.HealthCheckService;
import com.JeongGyul.HomeOps.domain.settings.repository.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthCheckScheduler {

    private static final String UP_KEY = "service:%d:up";
    private static final String LATENCY_KEY = "service:%d:latency";
    private static final String FAILS_KEY = "service:%d:fails";
    private static final String LAST_CHECK_KEY = "service:%d:lastCheck";

    private final MonitoredServiceRepository serviceRepository;
    private final HealthCheckLogRepository logRepository;
    private final HealthCheckService healthCheckService;
    private final AppSettingsRepository settingsRepository;
    private final StringRedisTemplate redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 10000)
    public void runHealthChecks() {
        int failThreshold = settingsRepository.findById(1L)
                .map(s -> s.getFailThreshold())
                .orElse(2);

        List<MonitoredService> services = serviceRepository.findAllByPausedFalse();
        long now = System.currentTimeMillis();

        for (MonitoredService service : services) {
            if (!isDue(service, now)) continue;

            try {
                HealthCheckResult result = healthCheckService.check(service);
                processResult(service, result, failThreshold);
                updateLastCheck(service.getId(), now);
            } catch (Exception e) {
                log.error("헬스체크 중 오류 발생 - 서비스 ID: {}", service.getId(), e);
            }
        }
    }

    private boolean isDue(MonitoredService service, long now) {
        String lastCheckStr = redisTemplate.opsForValue().get(
                String.format(LAST_CHECK_KEY, service.getId()));
        if (lastCheckStr == null) return true;
        long lastCheck = Long.parseLong(lastCheckStr);
        return (now - lastCheck) >= service.getCheckInterval() * 1000L;
    }

    private void processResult(MonitoredService service, HealthCheckResult result, int failThreshold) {
        Long id = service.getId();
        String prevUpStr = redisTemplate.opsForValue().get(String.format(UP_KEY, id));
        boolean prevUp = !"false".equals(prevUpStr);

        if (result.up()) {
            redisTemplate.opsForValue().set(String.format(UP_KEY, id), "true");
            if (result.latency() != null) {
                redisTemplate.opsForValue().set(String.format(LATENCY_KEY, id), String.valueOf(result.latency()));
            }
            redisTemplate.delete(String.format(FAILS_KEY, id));

            if (!prevUp && prevUpStr != null) {
                logRepository.save(HeathCheckLog.builder()
                        .monitoredService(service)
                        .eventType(EventType.RECOVER)
                        .description("서비스가 복구되었습니다.")
                        .build());
                eventPublisher.publishEvent(new ServiceRecoveredEvent(id, service.getName()));
                log.info("서비스 복구: {}", service.getName());
            }
        } else {
            long fails = incrementFails(id);
            redisTemplate.opsForValue().set(String.format(LATENCY_KEY, id), "");

            if (fails >= failThreshold && prevUp) {
                redisTemplate.opsForValue().set(String.format(UP_KEY, id), "false");
                logRepository.save(HeathCheckLog.builder()
                        .monitoredService(service)
                        .eventType(EventType.CRASH)
                        .description("서비스가 다운되었습니다.")
                        .build());
                eventPublisher.publishEvent(new ServiceCrashedEvent(id, service.getName()));
                log.warn("서비스 다운 감지: {}", service.getName());
            }
        }
    }

    private long incrementFails(Long serviceId) {
        String key = String.format(FAILS_KEY, serviceId);
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        return count != null ? count : 1;
    }

    private void updateLastCheck(Long serviceId, long now) {
        redisTemplate.opsForValue().set(
                String.format(LAST_CHECK_KEY, serviceId),
                String.valueOf(now));
    }
}
