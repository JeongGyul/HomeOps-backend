package com.JeongGyul.HomeOps.domain.monitoring.controller;

import com.JeongGyul.HomeOps.domain.monitoring.dto.*;
import com.JeongGyul.HomeOps.domain.monitoring.scheduler.ResourceScheduler;
import com.JeongGyul.HomeOps.domain.monitoring.service.MonitoredServiceService;
import com.JeongGyul.HomeOps.domain.monitoring.service.SseEmitterService;
import com.JeongGyul.HomeOps.global.apiPayload.ApiResponse;
import com.JeongGyul.HomeOps.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MonitoringController implements MonitoringControllerDocs {

    private final MonitoredServiceService serviceService;
    private final SseEmitterService sseEmitterService;
    private final ResourceScheduler resourceScheduler;

    @Override
    @GetMapping("/api/dashboard/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, serviceService.getSummary()));
    }

    @Override
    @GetMapping("/api/dashboard/resources")
    public ResponseEntity<ApiResponse<ResourceResponse>> getResources() {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, resourceScheduler.getLatest()));
    }

    @Override
    @GetMapping(value = "/api/dashboard/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseEmitterService.subscribe();
    }

    @Override
    @GetMapping("/api/services")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getServices() {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, serviceService.getAll()));
    }

    @Override
    @PostMapping("/api/services")
    public ResponseEntity<ApiResponse<ServiceResponse>> createService(
            @Valid @RequestBody ServiceCreateRequest request
    ) {
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(
                GeneralSuccessCode.CREATED, serviceService.create(request)));
    }

    @Override
    @PutMapping("/api/services/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, serviceService.update(id, request)));
    }

    @Override
    @DeleteMapping("/api/services/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(
            @PathVariable Long id
    ) {
        serviceService.delete(id);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode.OK, null));
    }

    @Override
    @PatchMapping("/api/services/{id}/pause")
    public ResponseEntity<ApiResponse<ServiceResponse>> togglePause(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                GeneralSuccessCode.OK, serviceService.togglePause(id)));
    }
}
