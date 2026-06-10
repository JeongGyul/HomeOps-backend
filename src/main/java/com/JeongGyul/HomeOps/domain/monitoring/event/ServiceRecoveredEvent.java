package com.JeongGyul.HomeOps.domain.monitoring.event;

import lombok.Getter;

@Getter
public class ServiceRecoveredEvent {

    private final Long serviceId;
    private final String serviceName;

    public ServiceRecoveredEvent(Long serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }
}
