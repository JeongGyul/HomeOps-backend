package com.JeongGyul.HomeOps.domain.settings.repository;

import com.JeongGyul.HomeOps.domain.settings.entity.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
