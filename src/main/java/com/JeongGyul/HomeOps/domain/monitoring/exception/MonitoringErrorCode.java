package com.JeongGyul.HomeOps.domain.monitoring.exception;

import com.JeongGyul.HomeOps.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MonitoringErrorCode implements BaseErrorCode {

    SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "MON404_1", "서비스를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
