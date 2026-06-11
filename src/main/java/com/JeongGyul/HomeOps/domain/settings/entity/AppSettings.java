package com.JeongGyul.HomeOps.domain.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "app_settings")
public class AppSettings {

    @Id
    private Long id;

    @Builder.Default
    @Column(name = "notify_crash", nullable = false)
    private boolean notifyCrash = true;

    @Builder.Default
    @Column(name = "notify_recover", nullable = false)
    private boolean notifyRecover = true;

    @Builder.Default
    @Column(name = "fail_threshold", nullable = false)
    private int failThreshold = 2;

    @Builder.Default
    @Column(name = "default_interval", nullable = false)
    private int defaultInterval = 30;

    public void update(boolean notifyCrash, boolean notifyRecover, int failThreshold, int defaultInterval) {
        this.notifyCrash = notifyCrash;
        this.notifyRecover = notifyRecover;
        this.failThreshold = failThreshold;
        this.defaultInterval = defaultInterval;
    }
}
