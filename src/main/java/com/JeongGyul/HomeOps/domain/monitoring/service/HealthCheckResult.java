package com.JeongGyul.HomeOps.domain.monitoring.service;

public record HealthCheckResult(boolean up, Long latency) {

    public static HealthCheckResult down() {
        return new HealthCheckResult(false, null);
    }
}
