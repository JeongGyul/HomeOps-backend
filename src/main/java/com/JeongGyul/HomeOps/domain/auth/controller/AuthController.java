package com.JeongGyul.HomeOps.domain.auth.controller;

import com.JeongGyul.HomeOps.domain.auth.dto.LoginRequest;
import com.JeongGyul.HomeOps.domain.auth.dto.RefreshRequest;
import com.JeongGyul.HomeOps.domain.auth.dto.TokenResponse;
import com.JeongGyul.HomeOps.domain.auth.service.AuthService;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import com.JeongGyul.HomeOps.global.apiPayload.code.GeneralSuccessCode;
import com.JeongGyul.HomeOps.global.security.principal.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, authService.login(request)));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String token = resolveToken(request);
        authService.logout(token, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, null));
    }

    @Override
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK,
                authService.refresh(request, Long.parseLong(userDetails.getUsername()))));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return "";
    }
}
