package com.JeongGyul.HomeOps.domain.notification.entity;

import com.JeongGyul.HomeOps.domain.monitoring.entity.MonitoredService;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "service_webhook")
public class ServiceWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id",  nullable = false)
    private MonitoredService monitoredService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webhook_id",  nullable = false)
    private Webhook webhook;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
