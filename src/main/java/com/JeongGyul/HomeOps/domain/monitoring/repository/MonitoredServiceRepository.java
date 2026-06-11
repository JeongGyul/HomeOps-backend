package com.JeongGyul.HomeOps.domain.monitoring.repository;

import com.JeongGyul.HomeOps.domain.monitoring.entity.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
    List<MonitoredService> findAllByPausedFalse();
}
