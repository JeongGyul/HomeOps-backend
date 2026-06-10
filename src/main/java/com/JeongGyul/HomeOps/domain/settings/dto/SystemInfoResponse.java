package com.JeongGyul.HomeOps.domain.settings.dto;

public record SystemInfoResponse(
        String hostname,
        String localIp,
        String os,
        long uptimeSeconds
) {}
