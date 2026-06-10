package com.JeongGyul.HomeOps.domain.monitoring.entity;

import com.JeongGyul.HomeOps.domain.monitoring.enums.CheckType;
import com.JeongGyul.HomeOps.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "service")
public class MonitoredService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", nullable = false, length = 20)
    private CheckType checkType;

    @Column(nullable = false)
    private String target;

    @Builder.Default
    @Column(name = "check_interval", nullable = false)
    private int checkInterval = 30;

    @Builder.Default
    @Column(nullable = false)
    private boolean paused = false;

    public void update(String name, CheckType checkType, String target, int checkInterval) {
        this.name = name;
        this.checkType = checkType;
        this.target = target;
        this.checkInterval = checkInterval;
    }

    public void togglePause() {
        this.paused = !this.paused;
    }

}
