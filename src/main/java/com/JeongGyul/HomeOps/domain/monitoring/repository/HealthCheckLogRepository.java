package com.JeongGyul.HomeOps.domain.monitoring.repository;

import com.JeongGyul.HomeOps.domain.monitoring.entity.HeathCheckLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HealthCheckLogRepository extends JpaRepository<HeathCheckLog, Long> {
    Page<HeathCheckLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Modifying
    @Query("DELETE FROM HeathCheckLog h WHERE h.monitoredService.id = :serviceId")
    void deleteAllByMonitoredServiceId(@Param("serviceId") Long serviceId);
}
