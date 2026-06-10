package com.JeongGyul.HomeOps.domain.notification.repository;

import com.JeongGyul.HomeOps.domain.notification.entity.ServiceWebhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceWebhookRepository extends JpaRepository<ServiceWebhook, Long> {
    List<ServiceWebhook> findAllByWebhookId(Long webhookId);
    List<ServiceWebhook> findAllByMonitoredServiceId(Long serviceId);
    void deleteAllByWebhookId(Long webhookId);
}
