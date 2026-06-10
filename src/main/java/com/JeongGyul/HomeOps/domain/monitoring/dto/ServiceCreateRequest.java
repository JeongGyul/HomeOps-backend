package com.JeongGyul.HomeOps.domain.monitoring.dto;

import com.JeongGyul.HomeOps.domain.monitoring.enums.CheckType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "서비스 등록 요청")
public record ServiceCreateRequest(
        @Schema(description = "서비스 이름", example = "Nginx")
        @NotBlank(message = "서비스 이름을 입력해주세요.")
        String name,

        @Schema(description = "체크 방식 (HTTP / TCP / PROCESS)", example = "HTTP")
        @NotNull(message = "체크 방식을 선택해주세요.")
        CheckType checkType,

        @Schema(description = "대상 URL 또는 IP:Port 또는 프로세스명",
                example = "http://localhost:80")
        @NotBlank(message = "대상 URL 또는 IP:Port를 입력해주세요.")
        String target,

        @Schema(description = "헬스체크 주기 (초)", example = "30")
        @Min(value = 5, message = "체크 주기는 최소 5초 이상이어야 합니다.")
        int checkInterval
) {
}
