package com.JeongGyul.HomeOps.domain.notification.service;

import com.JeongGyul.HomeOps.domain.member.entity.Member;
import com.JeongGyul.HomeOps.domain.member.repository.MemberRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final ServiceWebhookRepository serviceWebhookRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<WebhookResponse> getAll(Long memberId) {
        List<Webhook> webhooks = webhookRepository.findAllByMemberId(memberId);
        List<Long> webhookIds = webhooks.stream().map(Webhook::getId).toList();

        Map<Long, List<ServiceWebhook>> assignmentMap = webhookIds.isEmpty()
                ? Map.of()
                : serviceWebhookRepository.findAllByWebhookId(webhookIds.get(0))
                        .stream().collect(Collectors.groupingBy(sw -> sw.getWebhook().getId()));

        return webhooks.stream()
                .map(w -> WebhookResponse.of(w,
                        serviceWebhookRepository.findAllByWebhookId(w.getId())))
                .toList();
    }

    @Transactional
    public WebhookResponse create(Long memberId, WebhookCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(NotificationErrorCode.WEBHOOK_NOT_FOUND));

        Webhook webhook = Webhook.builder()
                .member(member)
                .name(request.name())
                .url(request.url())
                .targetAll(request.targetAll())
                .build();
        webhookRepository.save(webhook);

        saveServiceAssignments(webhook, request.serviceIds());

        return WebhookResponse.of(webhook,
                serviceWebhookRepository.findAllByWebhookId(webhook.getId()));
    }

    @Transactional
    public WebhookResponse update(Long id, Long memberId, WebhookUpdateRequest request) {
        Webhook webhook = findByIdAndMember(id, memberId);
        webhook.update(request.name(), request.url(), request.targetAll());

        serviceWebhookRepository.deleteAllByWebhookId(id);
        saveServiceAssignments(webhook, request.serviceIds());

        return WebhookResponse.of(webhook,
                serviceWebhookRepository.findAllByWebhookId(id));
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        Webhook webhook = findByIdAndMember(id, memberId);
        serviceWebhookRepository.deleteAllByWebhookId(id);
        webhookRepository.delete(webhook);
    }

    @Transactional
    public WebhookResponse toggle(Long id, Long memberId) {
        Webhook webhook = findByIdAndMember(id, memberId);
        webhook.toggleEnabled();
        return WebhookResponse.of(webhook,
                serviceWebhookRepository.findAllByWebhookId(id));
    }

    @Transactional(readOnly = true)
    public List<Webhook> findActiveWebhooksFor(Long serviceId) {
        List<Webhook> all = webhookRepository.findAllByEnabledTrue();
        return all.stream()
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

    private Webhook findByIdAndMember(Long id, Long memberId) {
        Webhook webhook = webhookRepository.findById(id)
                .orElseThrow(() -> new GeneralException(NotificationErrorCode.WEBHOOK_NOT_FOUND));
        if (!webhook.getMember().getId().equals(memberId)) {
            throw new GeneralException(NotificationErrorCode.WEBHOOK_NOT_FOUND);
        }
        return webhook;
    }
}
