package com.JeongGyul.HomeOps.domain.notification.exception;

import com.JeongGyul.HomeOps.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {

    WEBHOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI404_1", "웹훅을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
