package com.JeongGyul.HomeOps.domain.settings.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "앱 설정 수정 요청")
public record SettingsUpdateRequest(
        @Schema(description = "서비스 다운 시 알림 발송 여부", example = "true")
        boolean notifyCrash,

        @Schema(description = "서비스 복구 시 알림 발송 여부", example = "false")
        boolean notifyRecover,

        @Schema(description = "연속 실패 횟수 임계값", example = "3")
        @Min(value = 1, message = "실패 임계값은 최소 1 이상이어야 합니다.")
        int failThreshold,

        @Schema(description = "기본 헬스체크 주기 (초)", example = "30")
        @Min(value = 5, message = "기본 체크 주기는 최소 5초 이상이어야 합니다.")
        int defaultInterval
) {
}
