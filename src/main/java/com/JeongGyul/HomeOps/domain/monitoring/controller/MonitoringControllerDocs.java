package com.JeongGyul.HomeOps.domain.monitoring.controller;

import com.JeongGyul.HomeOps.domain.monitoring.dto.*;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "Monitoring", description = "모니터링 API — 대시보드 · 서비스 CRUD · 실시간 스트림")
@SecurityRequirement(name = "JWT")
public interface MonitoringControllerDocs {

    @Operation(summary = "대시보드 요약 조회",
            description = "전체 서비스의 UP/DOWN/일시중지 집계를 반환합니다.")
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
                                        "upCount": 6,
                                        "downCount": 1,
                                        "pausedCount": 1,
                                        "total": 8
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary();

    @Operation(summary = "서버 리소스 현황 조회",
            description = "가장 최근에 수집된 CPU · 메모리 · 온도 · 네트워크 스냅샷을 반환합니다.\n\n" +
                    "실시간 갱신이 필요하다면 `/api/dashboard/stream` SSE를 사용하세요.")
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
                                        "cpu": 34.2,
                                        "ram": 58.7,
                                        "temp": 52.1,
                                        "network": 2.14
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<ResourceResponse>> getResources();

    @Operation(summary = "실시간 대시보드 SSE 구독",
            description = "Server-Sent Events 스트림을 열어 리소스 · 서비스 상태 변화를 실시간으로 수신합니다.\n\n" +
                    "**이벤트 종류**\n" +
                    "- `event: resources` — CPU/메모리/온도/네트워크 (2초 주기)\n\n" +
                    "**브라우저 예시**\n```js\n" +
                    "const es = new EventSource('/api/dashboard/stream');\n" +
                    "es.addEventListener('resources', e => console.log(JSON.parse(e.data)));\n```\n\n" +
                    "> 인증 불필요 (브라우저 EventSource는 커스텀 헤더 불가)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SSE 스트림 연결 성공 (text/event-stream)")
    SseEmitter stream();

    @Operation(summary = "등록된 서비스 목록 조회",
            description = "Redis 캐시에서 실시간 UP/DOWN 상태와 Latency를 합쳐 반환합니다.")
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
                                          "name": "Nginx",
                                          "checkType": "HTTP",
                                          "target": "http://localhost:80",
                                          "checkInterval": 30,
                                          "paused": false,
                                          "up": true,
                                          "latency": 8,
                                          "createdAt": "2026-06-10T12:00:00"
                                        }
                                      ]
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<List<ServiceResponse>>> getServices();

    @Operation(summary = "서비스 등록",
            description = "새 서비스를 등록합니다.\n\n" +
                    "**체크 방식별 target 형식**\n" +
                    "- `HTTP` → `http://host:port/path`\n" +
                    "- `TCP` → `host:port` (예: `192.168.0.10:32400`)\n" +
                    "- `PROCESS` → 프로세스명 (예: `python3 bot.py`)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "서비스 등록 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON201",
                                      "message": "리소스가 성공적으로 생성되었습니다.",
                                      "result": {
                                        "id": 2,
                                        "name": "Plex Media",
                                        "checkType": "TCP",
                                        "target": "192.168.0.10:32400",
                                        "checkInterval": 30,
                                        "paused": false,
                                        "up": false,
                                        "latency": null,
                                        "createdAt": "2026-06-10T12:05:00"
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
                                        "target": "대상 URL 또는 IP:Port를 입력해주세요."
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<ServiceResponse>> createService(ServiceCreateRequest request);

    @Operation(summary = "서비스 수정",
            description = "서비스 이름, 체크 방식, 대상, 주기를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "서비스를 찾을 수 없음",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "MON404_1",
                                      "message": "서비스를 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @Parameter(description = "서비스 ID", example = "1") Long id,
            ServiceUpdateRequest request
    );

    @Operation(summary = "서비스 삭제",
            description = "서비스를 삭제하고 Redis 캐시도 함께 제거합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "서비스를 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<Void>> deleteService(
            @Parameter(description = "서비스 ID", example = "1") Long id
    );

    @Operation(summary = "서비스 일시중지 / 재개 토글",
            description = "일시중지 중인 서비스는 헬스체크 스케줄러에서 건너뜁니다.")
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
                                        "name": "Nginx",
                                        "checkType": "HTTP",
                                        "target": "http://localhost:80",
                                        "checkInterval": 30,
                                        "paused": true,
                                        "up": true,
                                        "latency": 8,
                                        "createdAt": "2026-06-10T12:00:00"
                                      }
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<ServiceResponse>> togglePause(
            @Parameter(description = "서비스 ID", example = "1") Long id
    );
}
