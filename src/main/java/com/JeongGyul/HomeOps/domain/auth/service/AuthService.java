package com.JeongGyul.HomeOps.domain.auth.service;

import com.JeongGyul.HomeOps.domain.auth.dto.LoginRequest;
import com.JeongGyul.HomeOps.domain.auth.dto.RefreshRequest;
import com.JeongGyul.HomeOps.domain.auth.dto.TokenResponse;
import com.JeongGyul.HomeOps.domain.auth.exception.code.AuthErrorCode;
import com.JeongGyul.HomeOps.domain.member.entity.Member;
import com.JeongGyul.HomeOps.domain.member.repository.MemberRepository;
import com.JeongGyul.HomeOps.global.apiPayload.exception.GeneralException;
import com.JeongGyul.HomeOps.global.security.jwt.JwtUtil;
import com.JeongGyul.HomeOps.global.security.principal.CustomUserDetails;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String BLACKLIST_PREFIX = "BL:";
    private static final String REFRESH_PREFIX = "RT:";

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByUsername(request.username())
                .orElseThrow(() -> new GeneralException(AuthErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new GeneralException(AuthErrorCode.LOGIN_FAILED);
        }

        CustomUserDetails userDetails = new CustomUserDetails(member);
        String accessToken = jwtUtil.createAccessToken(userDetails);
        String refreshToken = jwtUtil.createRefreshToken(userDetails);

        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + member.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    public void logout(String accessToken, Long memberId) {
        long remaining = jwtUtil.getExpiration(accessToken) - System.currentTimeMillis();
        if (remaining > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + accessToken,
                    "logout",
                    remaining,
                    TimeUnit.MILLISECONDS
            );
        }
        redisTemplate.delete(REFRESH_PREFIX + memberId);
    }

    public TokenResponse refresh(RefreshRequest request, Long memberId) {
        String stored = redisTemplate.opsForValue().get(REFRESH_PREFIX + memberId);
        if (stored == null) {
            throw new GeneralException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!stored.equals(request.refreshToken())) {
            throw new GeneralException(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        try {
            String category = jwtUtil.getCategory(request.refreshToken());
            if (!"refresh".equals(category)) {
                throw new GeneralException(AuthErrorCode.NOT_REFRESH_TOKEN);
            }
        } catch (JwtException e) {
            throw new GeneralException(AuthErrorCode.INVALID_TOKEN);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(AuthErrorCode.LOGIN_FAILED));

        CustomUserDetails userDetails = new CustomUserDetails(member);
        String newAccessToken = jwtUtil.createAccessToken(userDetails);
        String newRefreshToken = jwtUtil.createRefreshToken(userDetails);

        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + memberId,
                newRefreshToken,
                jwtUtil.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
