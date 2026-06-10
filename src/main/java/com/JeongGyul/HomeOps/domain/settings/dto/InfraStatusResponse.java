package com.JeongGyul.HomeOps.domain.settings.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인프라 연결 상태 응답")
public record InfraStatusResponse(
        @Schema(description = "MySQL 연결 상태 (connected / disconnected / error)", example = "connected")
        String mysqlStatus,

        @Schema(description = "Redis 연결 상태 (connected / disconnected)", example = "connected")
        String redisStatus
) {
}
