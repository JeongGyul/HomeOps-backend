package com.JeongGyul.HomeOps.domain.notification.repository;

import com.JeongGyul.HomeOps.domain.notification.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    List<Webhook> findAllByMemberId(Long memberId);
    List<Webhook> findAllByEnabledTrue();
}
