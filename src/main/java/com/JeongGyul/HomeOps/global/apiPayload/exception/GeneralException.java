package com.JeongGyul.HomeOps.global.apiPayload.exception;

import com.JeongGyul.HomeOps.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseErrorCode code;
}
