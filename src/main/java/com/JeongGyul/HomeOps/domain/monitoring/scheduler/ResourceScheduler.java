package com.JeongGyul.HomeOps.domain.monitoring.scheduler;

import com.JeongGyul.HomeOps.domain.monitoring.dto.ResourceResponse;
import com.JeongGyul.HomeOps.domain.monitoring.service.ResourceCollectorService;
import com.JeongGyul.HomeOps.domain.monitoring.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceScheduler {

    private final ResourceCollectorService resourceCollectorService;
    private final SseEmitterService sseEmitterService;

    private volatile ResourceResponse latest;

    @Scheduled(fixedRate = 2000)
    public void collectAndBroadcast() {
        try {
            ResourceResponse snapshot = resourceCollectorService.collect();
            latest = snapshot;
            sseEmitterService.broadcast("resources", snapshot);
        } catch (Exception e) {
            log.error("리소스 수집 중 오류 발생", e);
        }
    }

    public ResourceResponse getLatest() {
        return latest != null ? latest : new ResourceResponse(0, 0, 0, 0);
    }
}
