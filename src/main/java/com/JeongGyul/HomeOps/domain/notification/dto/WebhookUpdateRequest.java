package com.JeongGyul.HomeOps.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Webhook 수정 요청")
public record WebhookUpdateRequest(
        @Schema(description = "Webhook 이름", example = "코어 인프라 알림")
        @NotBlank(message = "웹훅 이름을 입력해주세요.")
        String name,

        @Schema(description = "Discord Webhook URL",
                example = "https://discord.com/api/webhooks/654321/fedcba")
        @NotBlank(message = "Discord Webhook URL을 입력해주세요.")
        String url,

        @Schema(description = "전체 서비스 대상 여부", example = "false")
        boolean targetAll,

        @Schema(description = "알림 받을 서비스 ID 목록 (targetAll=false 일 때)", example = "[1, 2]")
        List<Long> serviceIds
) {
}
