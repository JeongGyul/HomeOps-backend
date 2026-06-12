package com.JeongGyul.HomeOps.domain.notification.repository;

import com.JeongGyul.HomeOps.domain.notification.entity.ServiceWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceWebhookRepository extends JpaRepository<ServiceWebhook, Long> {
    List<ServiceWebhook> findAllByWebhookId(Long webhookId);
    void deleteAllByWebhookId(Long webhookId);

    @Modifying
    @Query("DELETE FROM ServiceWebhook sw WHERE sw.monitoredService.id = :serviceId")
    void deleteAllByMonitoredServiceId(@Param("serviceId") Long serviceId);
}
