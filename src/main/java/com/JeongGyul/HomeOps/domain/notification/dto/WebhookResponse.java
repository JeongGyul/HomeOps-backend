package com.JeongGyul.HomeOps.domain.notification.dto;

import com.JeongGyul.HomeOps.domain.notification.entity.ServiceWebhook;
import com.JeongGyul.HomeOps.domain.notification.entity.Webhook;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Webhook 응답")
public record WebhookResponse(
        @Schema(description = "Webhook ID", example = "1")
        Long id,

        @Schema(description = "Webhook 이름", example = "홈서버 전체 알림")
        String name,

        @Schema(description = "Discord Webhook URL",
                example = "https://discord.com/api/webhooks/123456/abcdef")
        String url,

        @Schema(description = "활성화 여부", example = "true")
        boolean enabled,

        @Schema(description = "전체 서비스 대상 여부", example = "true")
        boolean targetAll,

        @Schema(description = "연결된 서비스 ID 목록 (targetAll=true면 빈 배열)", example = "[]")
        List<Long> serviceIds,

        @Schema(description = "생성 시각", example = "2026-06-10T12:00:00")
        LocalDateTime createdAt
) {
    public static WebhookResponse of(Webhook webhook, List<ServiceWebhook> assignments) {
        List<Long> serviceIds = assignments.stream()
                .map(sw -> sw.getMonitoredService().getId())
                .toList();
        return new WebhookResponse(
                webhook.getId(),
                webhook.getName(),
                webhook.getUrl(),
                webhook.isEnabled(),
                webhook.isTargetAll(),
                serviceIds,
                webhook.getCreatedAt()
        );
    }
}
