package com.JeongGyul.HomeOps.domain.notification.service;

import com.JeongGyul.HomeOps.domain.monitoring.event.ServiceCrashedEvent;
import com.JeongGyul.HomeOps.domain.monitoring.event.ServiceRecoveredEvent;
import com.JeongGyul.HomeOps.domain.notification.entity.Webhook;
import com.JeongGyul.HomeOps.domain.settings.repository.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final WebhookService webhookService;
    private final AppSettingsRepository settingsRepository;
    private final RestTemplate restTemplate;

    @Async
    @EventListener
    public void onServiceCrashed(ServiceCrashedEvent event) {
        boolean notifyCrash = settingsRepository.findById(1L)
                .map(s -> s.isNotifyCrash())
                .orElse(true);
        if (!notifyCrash) return;

        List<Webhook> webhooks = webhookService.findActiveWebhooksFor(event.getServiceId());
        webhooks.forEach(wh -> sendDiscord(wh.getUrl(),
                "🔴 서비스 다운",
                "**" + event.getServiceName() + "** 이(가) 응답하지 않습니다.",
                0xF25C6E));
    }

    @Async
    @EventListener
    public void onServiceRecovered(ServiceRecoveredEvent event) {
        boolean notifyRecover = settingsRepository.findById(1L)
                .map(s -> s.isNotifyRecover())
                .orElse(true);
        if (!notifyRecover) return;

        List<Webhook> webhooks = webhookService.findActiveWebhooksFor(event.getServiceId());
        webhooks.forEach(wh -> sendDiscord(wh.getUrl(),
                "🟢 서비스 복구",
                "**" + event.getServiceName() + "** 이(가) 정상 상태로 복구되었습니다.",
                0x3DD68C));
    }

    public void sendTestMessage(String webhookUrl, String webhookName) {
        sendDiscord(webhookUrl,
                "🔔 HomeOps 테스트",
                "**" + webhookName + "** 웹훅 연결 테스트입니다.",
                0x5865F2);
    }

    private void sendDiscord(String url, String title, String description, int color) {
        try {
            Map<String, Object> embed = Map.of(
                    "title", title,
                    "description", description,
                    "color", color
            );
            Map<String, Object> body = Map.of("embeds", List.of(embed));
            restTemplate.postForEntity(url, body, Void.class);
        } catch (Exception e) {
            log.error("Discord 알림 전송 실패: {}", url, e);
        }
    }
}
