package com.JeongGyul.HomeOps.domain.notification.service;

import com.JeongGyul.HomeOps.domain.monitoring.repository.MonitoredServiceRepository;
import com.JeongGyul.HomeOps.domain.notification.dto.WebhookCreateRequest;
import com.JeongGyul.HomeOps.domain.notification.dto.WebhookResponse;
import com.JeongGyul.HomeOps.domain.notification.dto.WebhookUpdateRequest;
import com.JeongGyul.HomeOps.domain.notification.entity.ServiceWebhook;
import com.JeongGyul.HomeOps.domain.notification.entity.Webhook;
import com.JeongGyul.HomeOps.domain.notification.exception.NotificationErrorCode;
import com.JeongGyul.HomeOps.domain.notification.repository.ServiceWebhookRepository;
import com.JeongGyul.HomeOps.domain.notification.repository.WebhookRepository;
import com.JeongGyul.HomeOps.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final ServiceWebhookRepository serviceWebhookRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;

    @Transactional(readOnly = true)
    public List<WebhookResponse> getAll() {
        return webhookRepository.findAll().stream()
                .map(w -> WebhookResponse.of(w, serviceWebhookRepository.findAllByWebhookId(w.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public WebhookResponse getById(Long id) {
        Webhook webhook = findWebhookById(id);
        return WebhookResponse.of(webhook, serviceWebhookRepository.findAllByWebhookId(id));
    }

    @Transactional
    public WebhookResponse create(WebhookCreateRequest request) {
        Webhook webhook = Webhook.builder()
                .name(request.name())
                .url(request.url())
                .targetAll(request.targetAll())
                .build();
        webhookRepository.save(webhook);

        saveServiceAssignments(webhook, request.serviceIds());

        return WebhookResponse.of(webhook, serviceWebhookRepository.findAllByWebhookId(webhook.getId()));
    }

    @Transactional
    public WebhookResponse update(Long id, WebhookUpdateRequest request) {
        Webhook webhook = findWebhookById(id);
        webhook.update(request.name(), request.url(), request.targetAll());

        serviceWebhookRepository.deleteAllByWebhookId(id);
        saveServiceAssignments(webhook, request.serviceIds());

        return WebhookResponse.of(webhook, serviceWebhookRepository.findAllByWebhookId(id));
    }

    @Transactional
    public void delete(Long id) {
        Webhook webhook = findWebhookById(id);
        serviceWebhookRepository.deleteAllByWebhookId(id);
        webhookRepository.delete(webhook);
    }

    @Transactional
    public WebhookResponse toggle(Long id) {
        Webhook webhook = findWebhookById(id);
        webhook.toggleEnabled();
        return WebhookResponse.of(webhook, serviceWebhookRepository.findAllByWebhookId(id));
    }

    @Transactional(readOnly = true)
    public List<Webhook> findActiveWebhooksFor(Long serviceId) {
        return webhookRepository.findAllByEnabledTrue().stream()
                .filter(w -> {
                    if (w.isTargetAll()) return true;
                    return serviceWebhookRepository.findAllByWebhookId(w.getId())
                            .stream()
                            .anyMatch(sw -> sw.getMonitoredService().getId().equals(serviceId));
                })
                .toList();
    }

    private void saveServiceAssignments(Webhook webhook, List<Long> serviceIds) {
        if (!webhook.isTargetAll() && serviceIds != null && !serviceIds.isEmpty()) {
            serviceIds.forEach(serviceId ->
                    monitoredServiceRepository.findById(serviceId).ifPresent(service ->
                            serviceWebhookRepository.save(ServiceWebhook.builder()
                                    .monitoredService(service)
                                    .webhook(webhook)
                                    .build())
                    )
            );
        }
    }

    private Webhook findWebhookById(Long id) {
        return webhookRepository.findById(id)
                .orElseThrow(() -> new GeneralException(NotificationErrorCode.WEBHOOK_NOT_FOUND));
    }
}
