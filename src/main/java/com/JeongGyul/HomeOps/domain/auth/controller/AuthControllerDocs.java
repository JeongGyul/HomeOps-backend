package com.JeongGyul.HomeOps.domain.auth.controller;

import com.JeongGyul.HomeOps.domain.auth.dto.LoginRequest;
import com.JeongGyul.HomeOps.domain.auth.dto.RefreshRequest;
import com.JeongGyul.HomeOps.domain.auth.dto.TokenResponse;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import com.JeongGyul.HomeOps.global.security.principal.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 API — 로그인 / 로그아웃 / 토큰 재발급")
public interface AuthControllerDocs {

    @Operation(
            summary = "로그인",
            description = "아이디와 비밀번호로 로그인합니다. Access Token과 Refresh Token을 반환합니다.\n\n" +
                    "- Access Token은 `Authorization: Bearer <token>` 헤더에 담아 요청하세요.\n" +
                    "- Refresh Token은 Access Token 만료 시 재발급에 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.access...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh..."
                                      }
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "아이디 또는 비밀번호 불일치",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH401_7",
                                      "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
                                      "result": null
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<TokenResponse>> login(LoginRequest request);

    @Operation(
            summary = "로그아웃",
            description = "현재 Access Token을 블랙리스트에 등록하고 Refresh Token을 삭제합니다.\n\n" +
                    "만료된 Access Token으로도 요청 가능합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": null
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "토큰 없음 또는 유효하지 않은 토큰")
    })
    ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, CustomUserDetails userDetails);

    @Operation(
            summary = "Access Token 재발급",
            description = "만료된 Access Token과 유효한 Refresh Token을 사용해 새 토큰을 발급합니다.\n\n" +
                    "**요청 방법**\n" +
                    "- Header: `Authorization: Bearer <만료된_Access_Token>`\n" +
                    "- Body: `{ \"refreshToken\": \"...\" }`",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "재발급 성공",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMON200",
                                      "message": "요청이 성공적으로 처리되었습니다.",
                                      "result": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.new_access...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.new_refresh..."
                                      }
                                    }
                                    """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Refresh Token 만료 또는 불일치",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH401_8",
                                      "message": "리프레시 토큰이 만료되었거나 존재하지 않습니다.",
                                      "result": null
                                    }
                                    """)))
    })
    ResponseEntity<ApiResponse<TokenResponse>> refresh(RefreshRequest request, CustomUserDetails userDetails);
}
