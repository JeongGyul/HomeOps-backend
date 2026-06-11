package com.JeongGyul.HomeOps.domain.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "서버 리소스 현황")
public record ResourceResponse(
        @Schema(description = "CPU 사용률 (%)", example = "34.2")
        double cpu,

        @Schema(description = "메모리 사용률 (%)", example = "58.7")
        double ram,

        @Schema(description = "CPU 온도 (°C) — Linux는 실측, Mac/Windows는 추정값", example = "52.1")
        double temp,

        @Schema(description = "네트워크 처리량 (MB/s)", example = "2.14")
        double network
) {
}
