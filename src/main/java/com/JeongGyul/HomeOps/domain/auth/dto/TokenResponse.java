package com.JeongGyul.HomeOps.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 응답")
public record TokenResponse(
        @Schema(description = "Access Token (요청 헤더에 사용)", example = "eyJhbGciOiJIUzI1NiJ9.access...")
        String accessToken,

        @Schema(description = "Refresh Token (토큰 재발급 시 사용)", example = "eyJhbGciOiJIUzI1NiJ9.refresh...")
        String refreshToken
) {
}
