# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## AI Assistant Guidelines

- **Language:** All responses, explanations, comments, and commit messages MUST be written in **Korean (한국어)**.
- **Tone:** Be concise, professional, and focus on clean, maintainable code.

## Tech Stack

- **Java 21**, **Spring Boot 4.0.6**, **Gradle 9.4.1**
- **Spring Data JPA** + **MySQL** (Hibernate dialect, DDL auto: `update`)
- **Redis** (Used for high-speed, real-time caching of service monitoring states)
- **SpringDoc OpenAPI 3.0.2** — Swagger UI available at `/swagger-ui.html` when running

## Commands

```bash
# Build
./gradlew build

# Run application
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.JeongGyul.HomeOps.SomeTest"

# Clean build output
./gradlew clean
```

## Environment Setup

The app reads DB credentials and initial admin settings from environment variables. Create a `.env` file (already gitignored) or export:

```text
DB_URL=jdbc:mysql://localhost:3306/homeops
DB_USERNAME=<username>
DB_PASSWORD=<password>
REDIS_HOST=localhost
REDIS_PORT=6379
```

A local MySQL instance and Redis server must be running before starting the app.

## Project Overview

**HomeOps (Home-Infrastructure Operations Center)** — 라즈베리 파이 등 개인 서버에서 가동 중인 다양한 서비스의 상태를 실시간으로 모니터링하고 관리하는 중앙 허브.

### 주요 3대 기능

| 기능 | 설명 |
|------|------|
| **실시간 모니터링 (Observability)** | 등록된 서비스(HTTP/TCP/PROCESS)의 UP/DOWN 및 응답 속도 체크. CPU 사용률·RAM·온도 시각화 |
| **동적 서비스 관리 (Management)** | 웹 UI에서 서비스 CRUD — 이름, 체크 방식(HTTP/TCP/PROCESS), URL 또는 IP:Port 등록 |
| **자동 알림 시스템 (Notification)** | 서비스 다운 감지 시 Discord Webhook을 통해 즉시 알림 전송 |

### 기능 명세 요약

**Observability**
- 라즈베리 파이의 CPU 사용률·메모리·온도를 실시간 수집 → React 시계열 그래프/인디케이터로 시각화
- **멀티 환경 지원:** Linux(라즈베리 파이)에서는 실제 센서 파일 읽기, Mac/Windows 개발환경에서는 CPU 부하 기반 발열 추정 수식 적용
- 페이지 새로고침 없이 서비스 상태 변화가 대시보드 카드 UI에 실시간 반영 (Server-Sent Events 또는 WebSocket)

**Management**
- 서비스 등록 시 `체크방식(HTTP/TCP/PROCESS)` + `대상 URL 또는 IP:Port` 저장
- 대시보드는 그리드 카드 형태로 서비스 목록 표시, 각 카드에 UP/DOWN 상태와 Latency 표기

**Notification**
- 백엔드 스케줄러가 다운을 감지하면 `ServiceCrashedEvent` 발행 → notification 도메인이 수신하여 Discord Webhook 호출

---

## Architecture & Design Rules

The package root is `com.JeongGyul.HomeOps`. Code is organized by **Domain-Driven Design (DDD) principles**:

```text
src/main/java/com/JeongGyul/HomeOps/
├── domain/
│   ├── member/       # 관리자 인증 및 계정
│   ├── monitoring/   # 서비스 상태 체크 스케줄러, Redis 상태 캐싱, 하드웨어 리소스 수집
│   └── notification/ # Discord Webhook 알림 발송 (이벤트 리스너)
└── global/           # 공통 예외처리, 응답 래퍼, 설정, 유틸
```

### Domain Rules:
1. **Layered Structure:** Each domain must strictly follow `Controller → Service → Repository → Entity`.
2. **Decoupling (Event-Driven):** 도메인 간 직접 의존 금지. `monitoring` → `notification` 직접 호출 대신 **Spring ApplicationEvent** 사용 (예: `ServiceCrashedEvent` 발행 → notification 리스너가 수신).
3. **Data State Management:**
   - **MySQL:** 서비스 메타데이터(이름, 체크방식, URL), Webhook URL, 상태 전환 이력(CRASH/RECOVER)
   - **Redis:** 고빈도 실시간 상태 캐시(현재 UP/DOWN, Latency)

## Development Workflow

- **Commits:** Strictly follow **Conventional Commits** (e.g., `feat: ...`, `fix: ...`, `chore: ...`).
- **Entity Design:** Prefer `@Builder` for entities and avoid generic `@Setter`. Always design entities with explicit update methods.