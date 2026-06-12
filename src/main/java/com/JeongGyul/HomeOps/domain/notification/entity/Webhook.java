package com.JeongGyul.HomeOps.domain.notification.entity;

import com.JeongGyul.HomeOps.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "webhook")
public class Webhook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String url;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    @Builder.Default
    @Column(name = "target_all", nullable = false)
    private boolean targetAll = true;

    public void update(String name, String url, boolean targetAll) {
        this.name = name;
        this.url = url;
        this.targetAll = targetAll;
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
    }

}
