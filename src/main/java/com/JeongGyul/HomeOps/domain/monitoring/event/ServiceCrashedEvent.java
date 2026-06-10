package com.JeongGyul.HomeOps.domain.monitoring.event;

import lombok.Getter;

@Getter
public class ServiceCrashedEvent {

    private final Long serviceId;
    private final String serviceName;

    public ServiceCrashedEvent(Long serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }
}
