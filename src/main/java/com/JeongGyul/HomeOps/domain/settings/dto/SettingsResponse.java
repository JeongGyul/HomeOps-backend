package com.JeongGyul.HomeOps.domain.settings.dto;

import com.JeongGyul.HomeOps.domain.settings.entity.AppSettings;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "앱 설정 응답")
public record SettingsResponse(
        @Schema(description = "서비스 다운 시 Discord 알림 발송 여부", example = "true")
        boolean notifyCrash,

        @Schema(description = "서비스 복구 시 Discord 알림 발송 여부", example = "true")
        boolean notifyRecover,

        @Schema(description = "연속 실패 횟수 임계값 (이 횟수 이상 실패해야 다운으로 판정)", example = "2")
        int failThreshold,

        @Schema(description = "기본 헬스체크 주기 (초)", example = "30")
        int defaultInterval
) {
    public static SettingsResponse from(AppSettings settings) {
        return new SettingsResponse(
                settings.isNotifyCrash(),
                settings.isNotifyRecover(),
                settings.getFailThreshold(),
                settings.getDefaultInterval()
        );
    }

    public static SettingsResponse defaults() {
        return new SettingsResponse(true, true, 2, 30);
    }
}
