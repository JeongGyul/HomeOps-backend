package com.JeongGyul.HomeOps.domain.notification.controller;

import com.JeongGyul.HomeOps.domain.notification.dto.*;
import com.JeongGyul.HomeOps.domain.notification.service.NotificationService;
import com.JeongGyul.HomeOps.domain.notification.service.WebhookService;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import com.JeongGyul.HomeOps.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final WebhookService webhookService;
    private final NotificationService notificationService;

    @Override
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationHistoryResponse>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, notificationService.getHistory(page, size)));
    }

    @Override
    @GetMapping("/webhooks")
    public ResponseEntity<ApiResponse<List<WebhookResponse>>> getWebhooks() {
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, webhookService.getAll()));
    }

    @Override
    @PostMapping("/webhooks")
    public ResponseEntity<ApiResponse<WebhookResponse>> createWebhook(
            @Valid @RequestBody WebhookCreateRequest request
    ) {
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(
                GeneralSuccessCode.CREATED, webhookService.create(request)));
    }

    @Override
    @PutMapping("/webhooks/{id}")
    public ResponseEntity<ApiResponse<WebhookResponse>> updateWebhook(
            @PathVariable Long id,
            @Valid @RequestBody WebhookUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, webhookService.update(id, request)));
    }

    @Override
    @DeleteMapping("/webhooks/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWebhook(@PathVariable Long id) {
        webhookService.delete(id);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, null));
    }

    @Override
    @PatchMapping("/webhooks/{id}/toggle")
    public ResponseEntity<ApiResponse<WebhookResponse>> toggleWebhook(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, webhookService.toggle(id)));
    }

    @Override
    @PostMapping("/webhooks/{id}/test")
    public ResponseEntity<ApiResponse<Void>> testWebhook(@PathVariable Long id) {
        WebhookResponse webhook = webhookService.getById(id);
        notificationService.sendTestMessage(webhook.url(), webhook.name());
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, null));
    }
}
