package com.JeongGyul.HomeOps.domain.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대시보드 요약 정보")
public record DashboardSummaryResponse(
        @Schema(description = "정상 서비스 수", example = "6")
        int upCount,

        @Schema(description = "다운 서비스 수", example = "1")
        int downCount,

        @Schema(description = "일시중지 서비스 수", example = "1")
        int pausedCount,

        @Schema(description = "전체 서비스 수", example = "8")
        int total
) {
}
