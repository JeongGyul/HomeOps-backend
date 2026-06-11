package com.JeongGyul.HomeOps.domain.notification.dto;

import com.JeongGyul.HomeOps.domain.monitoring.entity.HeathCheckLog;
import com.JeongGyul.HomeOps.domain.monitoring.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "알림 이력 응답")
public record NotificationHistoryResponse(
        @Schema(description = "로그 ID", example = "1")
        Long id,

        @Schema(description = "서비스 이름", example = "Syncthing")
        String serviceName,

        @Schema(description = "이벤트 유형 (CRASH / RECOVER)", example = "CRASH")
        EventType eventType,

        @Schema(description = "상세 메시지", example = "서비스가 다운되었습니다.")
        String description,

        @Schema(description = "발생 시각", example = "2026-06-10T02:14:00")
        LocalDateTime createdAt
) {
    public static NotificationHistoryResponse from(HeathCheckLog log) {
        return new NotificationHistoryResponse(
                log.getId(),
                log.getMonitoredService().getName(),
                log.getEventType(),
                log.getDescription(),
                log.getCreatedAt()
        );
    }
}
