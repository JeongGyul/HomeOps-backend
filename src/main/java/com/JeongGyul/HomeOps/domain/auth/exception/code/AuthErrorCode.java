package com.JeongGyul.HomeOps.domain.auth.exception.code;

import com.JeongGyul.HomeOps.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // Access Token & Refresh Token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_1", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_2", "만료된 토큰입니다."),
    NOT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_3", "엑세스 토큰이 아닙니다."),
    NOT_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_4", "리프레시 토큰이 아닙니다."),
    TOKEN_IN_BLACKLIST(HttpStatus.UNAUTHORIZED, "AUTH401_5", "이미 로그아웃된 토큰입니다. 다시 로그인해주세요."),
    INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "AUTH401_6", "유효하지 않은 토큰 형식입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
