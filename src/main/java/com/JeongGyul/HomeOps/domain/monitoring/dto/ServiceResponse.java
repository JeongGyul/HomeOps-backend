package com.JeongGyul.HomeOps.domain.monitoring.dto;

import com.JeongGyul.HomeOps.domain.monitoring.entity.MonitoredService;
import com.JeongGyul.HomeOps.domain.monitoring.enums.CheckType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "서비스 응답")
public record ServiceResponse(
        @Schema(description = "서비스 ID", example = "1")
        Long id,

        @Schema(description = "서비스 이름", example = "Nginx")
        String name,

        @Schema(description = "체크 방식", example = "HTTP")
        CheckType checkType,

        @Schema(description = "체크 대상", example = "http://localhost:80")
        String target,

        @Schema(description = "체크 주기 (초)", example = "30")
        int checkInterval,

        @Schema(description = "일시중지 여부", example = "false")
        boolean paused,

        @Schema(description = "현재 상태 (UP/DOWN)", example = "true")
        boolean up,

        @Schema(description = "마지막 응답 시간 (ms), PROCESS 타입은 null", example = "42")
        Long latency,

        @Schema(description = "서비스 등록 시각", example = "2026-06-10T12:00:00")
        LocalDateTime createdAt
) {
    public static ServiceResponse of(MonitoredService service, boolean up, Long latency) {
        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getCheckType(),
                service.getTarget(),
                service.getCheckInterval(),
                service.isPaused(),
                up,
                latency,
                service.getCreatedAt()
        );
    }
}
