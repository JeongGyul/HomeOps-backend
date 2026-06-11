package com.JeongGyul.HomeOps.domain.settings.controller;

import com.JeongGyul.HomeOps.domain.settings.dto.InfraStatusResponse;
import com.JeongGyul.HomeOps.domain.settings.dto.SettingsResponse;
import com.JeongGyul.HomeOps.domain.settings.dto.SettingsUpdateRequest;
import com.JeongGyul.HomeOps.domain.settings.dto.SystemInfoResponse;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Settings", description = "설정 API — 알림 임계값 · 체크 주기 · 인프라 연결 상태")
@SecurityRequirement(name = "JWT")
public interface SettingsControllerDocs {

    @Operation(summary = "설정 조회",
            description = "알림 발송 여부, 실패 임계값, 기본 헬스체크 주기를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": {
                                        "notifyCrash": true,
                                        "notifyRecover": true,
                                        "failThreshold": 2,
                                        "defaultInterval": 30
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<SettingsResponse>> getSettings();

    @Operation(summary = "설정 수정",
            description = "알림 발송 여부, 실패 임계값, 기본 헬스체크 주기를 변경합니다.\n\n" +
                    "- `failThreshold`: 이 횟수만큼 연속으로 실패해야 CRASH 이벤트가 발생합니다.\n" +
                    "- `defaultInterval`: 신규 서비스 등록 시 기본값으로 사용됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": {
                                        "notifyCrash": true,
                                        "notifyRecover": false,
                                        "failThreshold": 3,
                                        "defaultInterval": 60
                                      }
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "VALID400_1",
                                      "message": "검증에 실패했습니다.",
                                      "result": {
                                        "failThreshold": "실패 임계값은 최소 1 이상이어야 합니다."
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<SettingsResponse>> updateSettings(SettingsUpdateRequest request);

    @Operation(summary = "인프라 연결 상태 조회",
            description = "MySQL과 Redis의 연결 상태를 실시간으로 확인합니다.\n\n" +
                    "**상태 값**\n" +
                    "- `connected` — 정상 연결\n" +
                    "- `disconnected` — 연결 불가\n" +
                    "- `error` — 연결됐으나 오류 발생")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": {
                                        "mysqlStatus": "connected",
                                        "redisStatus": "connected"
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<InfraStatusResponse>> getStatus();

    @Operation(summary = "시스템 정보 조회",
            description = "서버의 호스트명, 로컬 IP, OS, 가동 시간을 반환합니다.")
    ResponseEntity<ApiResponse<SystemInfoResponse>> getSystemInfo();
}
