package com.JeongGyul.HomeOps.domain.notification.controller;

import com.JeongGyul.HomeOps.domain.monitoring.entity.HeathCheckLog;
import com.JeongGyul.HomeOps.domain.monitoring.repository.HealthCheckLogRepository;
import com.JeongGyul.HomeOps.domain.notification.dto.*;
import com.JeongGyul.HomeOps.domain.notification.exception.NotificationErrorCode;
import com.JeongGyul.HomeOps.domain.notification.service.NotificationService;
import com.JeongGyul.HomeOps.domain.notification.service.WebhookService;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import com.JeongGyul.HomeOps.global.apiPayload.code.GeneralSuccessCode;
import com.JeongGyul.HomeOps.global.apiPayload.exception.GeneralException;
import com.JeongGyul.HomeOps.global.security.principal.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final WebhookService webhookService;
    private final NotificationService notificationService;
    private final HealthCheckLogRepository healthCheckLogRepository;

    @Override
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationHistoryResponse>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size
    ) {
        Page<HeathCheckLog> logs = healthCheckLogRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(page, size));
        List<NotificationHistoryResponse> result = logs.stream()
                .map(NotificationHistoryResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }

    @Override
    @GetMapping("/webhooks")
    public ResponseEntity<ApiResponse<List<WebhookResponse>>> getWebhooks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, webhookService.getAll(memberId)));
    }

    @Override
    @PostMapping("/webhooks")
    public ResponseEntity<ApiResponse<WebhookResponse>> createWebhook(
            @Valid @RequestBody WebhookCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(
                GeneralSuccessCode.CREATED, webhookService.create(memberId, request)));
    }

    @Override
    @PutMapping("/webhooks/{id}")
    public ResponseEntity<ApiResponse<WebhookResponse>> updateWebhook(
            @PathVariable Long id,
            @Valid @RequestBody WebhookUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, webhookService.update(id, memberId, request)));
    }

    @Override
    @DeleteMapping("/webhooks/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWebhook(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        webhookService.delete(id, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, null));
    }

    @Override
    @PatchMapping("/webhooks/{id}/toggle")
    public ResponseEntity<ApiResponse<WebhookResponse>> toggleWebhook(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, webhookService.toggle(id, memberId)));
    }

    @Override
    @PostMapping("/webhooks/{id}/test")
    public ResponseEntity<ApiResponse<Void>> testWebhook(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        WebhookResponse webhook = webhookService.getAll(memberId).stream()
                .filter(w -> w.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new GeneralException(NotificationErrorCode.WEBHOOK_NOT_FOUND));
        notificationService.sendTestMessage(webhook.url(), webhook.name());
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, null));
    }
}
