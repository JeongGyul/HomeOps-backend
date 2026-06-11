package com.JeongGyul.HomeOps.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Webhook 등록 요청")
public record WebhookCreateRequest(
        @Schema(description = "Webhook 이름", example = "홈서버 전체 알림")
        @NotBlank(message = "웹훅 이름을 입력해주세요.")
        String name,

        @Schema(description = "Discord Webhook URL",
                example = "https://discord.com/api/webhooks/123456/abcdef")
        @NotBlank(message = "Discord Webhook URL을 입력해주세요.")
        String url,

        @Schema(description = "전체 서비스 대상 여부. true면 serviceIds 무시", example = "true")
        boolean targetAll,

        @Schema(description = "알림 받을 서비스 ID 목록 (targetAll=false 일 때 사용)", example = "[1, 3]")
        List<Long> serviceIds
) {
}
