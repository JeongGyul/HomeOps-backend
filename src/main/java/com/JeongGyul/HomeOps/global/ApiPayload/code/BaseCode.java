package com.JeongGyul.HomeOps.global.ApiPayload.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    HttpStatus getStatus();
    String getCode();
    String getMessage();

}
