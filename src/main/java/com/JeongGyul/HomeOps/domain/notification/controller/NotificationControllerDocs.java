package com.JeongGyul.HomeOps.domain.notification.controller;

import com.JeongGyul.HomeOps.domain.notification.dto.*;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Notification", description = "알림 API — 이벤트 이력 조회 · Discord Webhook CRUD")
@SecurityRequirement(name = "JWT")
public interface NotificationControllerDocs {

    @Operation(summary = "알림 이력 조회",
            description = "서비스 다운(CRASH) · 복구(RECOVER) 이벤트 이력을 최신순으로 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": [
                                        {
                                          "id": 1,
                                          "serviceName": "Syncthing",
                                          "eventType": "CRASH",
                                          "description": "서비스가 다운되었습니다.",
                                          "createdAt": "2026-06-10T02:14:00"
                                        },
                                        {
                                          "id": 2,
                                          "serviceName": "Syncthing",
                                          "eventType": "RECOVER",
                                          "description": "서비스가 복구되었습니다.",
                                          "createdAt": "2026-06-10T03:01:00"
                                        }
                                      ]
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<List<NotificationHistoryResponse>>> getHistory(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") int page,
            @Parameter(description = "페이지 크기", example = "40") int size
    );

    @Operation(summary = "Webhook 목록 조회",
            description = "등록된 Webhook 목록과 연결된 서비스 ID를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": [
                                        {
                                          "id": 1,
                                          "name": "홈서버 전체 알림",
                                          "url": "https://discord.com/api/webhooks/123/abc",
                                          "enabled": true,
                                          "targetAll": true,
                                          "serviceIds": [],
                                          "createdAt": "2026-06-10T12:00:00"
                                        }
                                      ]
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<List<WebhookResponse>>> getWebhooks();

    @Operation(summary = "Webhook 등록",
            description = "Discord Webhook을 등록합니다.\n\n" +
                    "- `targetAll: true` → 모든 서비스 다운 시 알림\n" +
                    "- `targetAll: false` → `serviceIds`에 지정된 서비스만 알림")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "등록 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON201",
                                      "message": "리소스가 성공적으로 생성되었습니다.",
                                      "result": {
                                        "id": 2,
                                        "name": "미디어 채널",
                                        "url": "https://discord.com/api/webhooks/789/xyz",
                                        "enabled": true,
                                        "targetAll": false,
                                        "serviceIds": [3, 5],
                                        "createdAt": "2026-06-10T13:00:00"
                                      }
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패")
    })
    ResponseEntity<ApiResponse<WebhookResponse>> createWebhook(WebhookCreateRequest request);

    @Operation(summary = "Webhook 수정",
            description = "Webhook 이름, URL, 대상 서비스를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "Webhook을 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "NOTI404_1",
                                      "message": "웹훅을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<WebhookResponse>> updateWebhook(
            @Parameter(description = "Webhook ID", example = "1") Long id,
            WebhookUpdateRequest request
    );

    @Operation(summary = "Webhook 삭제",
            description = "Webhook과 연결된 서비스 매핑 데이터를 함께 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Webhook을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<Void>> deleteWebhook(
            @Parameter(description = "Webhook ID", example = "1") Long id
    );

    @Operation(summary = "Webhook 활성화 / 비활성화 토글",
            description = "비활성화된 Webhook은 서비스 다운 시 Discord 메시지를 전송하지 않습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "토글 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": {
                                        "id": 1,
                                        "name": "홈서버 전체 알림",
                                        "url": "https://discord.com/api/webhooks/123/abc",
                                        "enabled": false,
                                        "targetAll": true,
                                        "serviceIds": [],
                                        "createdAt": "2026-06-10T12:00:00"
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<WebhookResponse>> toggleWebhook(
            @Parameter(description = "Webhook ID", example = "1") Long id
    );

    @Operation(summary = "Webhook 테스트 메시지 전송",
            description = "Discord 채널로 테스트 메시지를 전송해 Webhook URL이 유효한지 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "테스트 메시지 전송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Webhook을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<Void>> testWebhook(
            @Parameter(description = "Webhook ID", example = "1") Long id
    );
}
