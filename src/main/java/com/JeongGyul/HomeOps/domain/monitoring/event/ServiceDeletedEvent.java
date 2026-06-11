package com.JeongGyul.HomeOps.domain.monitoring.event;

import lombok.Getter;

@Getter
public class ServiceDeletedEvent {

    private final Long serviceId;

    public ServiceDeletedEvent(Long serviceId) {
        this.serviceId = serviceId;
    }
}
