package com.JeongGyul.HomeOps.domain.settings.controller;

import com.JeongGyul.HomeOps.domain.settings.dto.InfraStatusResponse;
import com.JeongGyul.HomeOps.domain.settings.dto.SettingsResponse;
import com.JeongGyul.HomeOps.domain.settings.dto.SettingsUpdateRequest;
import com.JeongGyul.HomeOps.domain.settings.dto.SystemInfoResponse;
import com.JeongGyul.HomeOps.domain.settings.service.SettingsService;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import com.JeongGyul.HomeOps.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController implements SettingsControllerDocs {

    private final SettingsService settingsService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<SettingsResponse>> getSettings() {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, settingsService.getSettings()));
    }

    @Override
    @PatchMapping
    public ResponseEntity<ApiResponse<SettingsResponse>> updateSettings(
            @Valid @RequestBody SettingsUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, settingsService.updateSettings(request)));
    }

    @Override
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<InfraStatusResponse>> getStatus() {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, settingsService.getStatus()));
    }

    @Override
    @GetMapping("/system")
    public ResponseEntity<ApiResponse<SystemInfoResponse>> getSystemInfo() {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, settingsService.getSystemInfo()));
    }
}
