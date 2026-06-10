package com.JeongGyul.HomeOps.domain.monitoring.repository;

import com.JeongGyul.HomeOps.domain.monitoring.entity.HeathCheckLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckLogRepository extends JpaRepository<HeathCheckLog, Long> {
    Page<HeathCheckLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
