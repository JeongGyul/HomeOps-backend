package com.JeongGyul.HomeOps.global.config;

import com.JeongGyul.HomeOps.domain.member.entity.Member;
import com.JeongGyul.HomeOps.domain.member.repository.MemberRepository;
import com.JeongGyul.HomeOps.domain.settings.entity.AppSettings;
import com.JeongGyul.HomeOps.domain.settings.repository.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final AppSettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:homeops1234}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (memberRepository.existsByUsername(adminUsername)) {
            return;
        }
        Member admin = Member.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .email("admin@homeops.local")
                .build();
        memberRepository.save(admin);
        log.info("관리자 계정 생성 완료: {}", adminUsername);

        if (settingsRepository.count() == 0) {
            settingsRepository.save(AppSettings.builder()
                    .id(1L)
                    .notifyCrash(true)
                    .notifyRecover(true)
                    .failThreshold(2)
                    .defaultInterval(30)
                    .build());
        }
    }
}
