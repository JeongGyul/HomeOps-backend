package com.JeongGyul.HomeOps.domain.monitoring.service;

import com.JeongGyul.HomeOps.domain.monitoring.dto.*;
import com.JeongGyul.HomeOps.domain.monitoring.entity.MonitoredService;
import com.JeongGyul.HomeOps.domain.monitoring.exception.MonitoringErrorCode;
import com.JeongGyul.HomeOps.domain.monitoring.repository.MonitoredServiceRepository;
import com.JeongGyul.HomeOps.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoredServiceService {

    private static final String UP_KEY = "service:%d:up";
    private static final String LATENCY_KEY = "service:%d:latency";

    private final MonitoredServiceRepository serviceRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public List<ServiceResponse> getAll() {
        return serviceRepository.findAll().stream()
                .map(this::enrichWithStatus)
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        List<MonitoredService> all = serviceRepository.findAll();
        int paused = (int) all.stream().filter(MonitoredService::isPaused).count();
        int up = (int) all.stream()
                .filter(s -> !s.isPaused())
                .filter(s -> isUp(s.getId()))
                .count();
        int down = (int) all.stream()
                .filter(s -> !s.isPaused())
                .filter(s -> !isUp(s.getId()))
                .count();
        return new DashboardSummaryResponse(up, down, paused, all.size());
    }

    @Transactional
    public ServiceResponse create(ServiceCreateRequest request) {
        MonitoredService service = MonitoredService.builder()
                .name(request.name())
                .checkType(request.checkType())
                .target(request.target())
                .checkInterval(request.checkInterval())
                .build();
        MonitoredService saved = serviceRepository.save(service);
        return enrichWithStatus(saved);
    }

    @Transactional
    public ServiceResponse update(Long id, ServiceUpdateRequest request) {
        MonitoredService service = findById(id);
        service.update(request.name(), request.checkType(), request.target(), request.checkInterval());
        return enrichWithStatus(service);
    }

    @Transactional
    public void delete(Long id) {
        MonitoredService service = findById(id);
        clearStatusCache(id);
        serviceRepository.delete(service);
    }

    @Transactional
    public ServiceResponse togglePause(Long id) {
        MonitoredService service = findById(id);
        service.togglePause();
        return enrichWithStatus(service);
    }

    private ServiceResponse enrichWithStatus(MonitoredService service) {
        boolean up = isUp(service.getId());
        Long latency = getLatency(service.getId());
        return ServiceResponse.of(service, up, latency);
    }

    private boolean isUp(Long serviceId) {
        String val = redisTemplate.opsForValue().get(String.format(UP_KEY, serviceId));
        return "true".equals(val);
    }

    private Long getLatency(Long serviceId) {
        String val = redisTemplate.opsForValue().get(String.format(LATENCY_KEY, serviceId));
        if (val == null) return null;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void clearStatusCache(Long serviceId) {
        redisTemplate.delete(String.format(UP_KEY, serviceId));
        redisTemplate.delete(String.format(LATENCY_KEY, serviceId));
        redisTemplate.delete("service:" + serviceId + ":fails");
        redisTemplate.delete("service:" + serviceId + ":lastCheck");
    }

    private MonitoredService findById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new GeneralException(MonitoringErrorCode.SERVICE_NOT_FOUND));
    }
}
