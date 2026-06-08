package com.JeongGyul.HomeOps.domain.member.repository;

import com.JeongGyul.HomeOps.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
