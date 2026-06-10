package com.JeongGyul.HomeOps.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(description = "관리자 아이디", example = "admin")
        @NotBlank(message = "아이디를 입력해주세요.")
        String username,

        @Schema(description = "비밀번호", example = "homeops1234")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
