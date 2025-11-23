<div align="center">
  <h1>OnHaru</h1>
  <h3>시니어 복약 동행을 돕는 AI 기반 케어 플랫폼</h3>
  <p>
    처방전 등록부터 복약 알림, 복약 기록, 리포트까지 한 곳에서 관리합니다.<br />
    가족과 케어매니저가 안심하고 복약 여정을 함께할 수 있도록 돕습니다.
  </p>
</div>

---

## 🔗 Links & Assets

- **API 스펙 & 기능 문서**: [`/docs`](docs) 디렉터리 (처방전, 복약 기록, 리포트 등)
- **Run (local)**: `./gradlew bootRun`
- **Sample `.env` keys**: `application.yaml` 의 `${...}` 항목 참고 (DB, JWT, OpenAI, OCR 등)

---

## 🧐 Why OnHaru?

> "약은 꼭 챙겨 드셨나요?"를 묻는 대신, **OnHaru**가 처방전부터 복약 리포트까지 자동으로 챙깁니다.

시니어/케어 수급자의 복약 루틴은 약 처방-약 보관-알람-복약 확인-리포트까지 많은 사람이 관여해야 하는 여정입니다. OnHaru는
처방전을 기준으로 **약·스케줄·알림·복약 로그**를 일관된 데이터로 관리하고, AI를 통해 리포트/설명을 자동 작성하여 케어 부담을 줄입니다.

---

## ✨ Key Features

### 1. 처방전 캡처 & OCR 후처리
- `POST /api/prescriptions` 로 시니어·병원 정보와 약/스케줄을 한 번에 등록.
- `POST /api/prescriptions/upload` 는 Naver OCR + Spring AI 시나리오로 이미지 → 구조화 JSON 변환.
- 약 메모, 총 수량, 기간, 시간대 태그를 그대로 스케줄에 반영하여 등록 과정을 단순화합니다.

### 2. 복약 스케줄 & 실시간 알림
- `medicine_schedules` 를 기반으로 정해진 요일/시간대마다 푸시 알림을 발송 (`MedicineAlarmScheduler`).
- `PushSubscriptionService` 가 디바이스 구독을 관리해 시니어별로 맞춤 알림을 전송합니다.

### 3. Taking Log & 월간 캘린더
- 알림이 발송되면 `MedicineAlarmEvent → TakingLogService` 흐름으로 슬롯을 자동 생성.
- `GET /api/taking-logs/calendar` 는 실제 기록 + 미래 스케줄을 결합해 DayStatus(NONE/PLANNED/PARTIAL/COMPLETE/MISSED)를 계산합니다.
- 미래 날짜라도 스케줄이 존재하면 PLANNED로 표시되어 케어 계획을 미리 확인할 수 있습니다.

### 4. AI 복약 리포트 & 통계
- `GET /api/reports` 는 TakingLog 통계를 기반으로 ReportPayload를 만들고, Spring AI(OpenAI)로 요약·위험 태그·제안을 생성합니다.
- 리포트가 없으면 즉시 생성 후 저장하여, 월별 복약 현황을 손쉽게 공유할 수 있습니다.

### 5. 케어 네트워크 & 사용자 권한
- `caregiver` / `senior` 도메인이 서로 연결되어 케어매니저, 가족, 시니어 계정을 분리 관리합니다.
- JWT 기반 인증과 역할(Role) 검증으로 API 접근을 제어합니다.

---

## 🛠 Tech Stack

| 구분 | 기술 |
| --- | --- |
| **Core Backend** | Java 21, Spring Boot 3.3, Spring Validation, Spring Data JPA |
| **Data Layer** | MySQL (InnoDB), HikariCP |
| **Messaging & Scheduling** | Spring Events, `@Scheduled` Cron 알람, ApplicationEventPublisher |
| **AI & OCR** | Spring AI(OpenAI Chat Completions), Naver Clova OCR Client |
| **Push & Auth** | Web Push (VAPID), JWT (jjwt 0.12.x), Spring Security Crypto |
| **Build & Tooling** | Gradle, Dockerfile, GitHub Actions-ready 구조 |

---

## 🏗 Architecture Snapshot

```
PrescriptionController
   └─ PrescriptionService ──┬─ PrescriptionMedicationService ──> MedicineSchedule / Medicine
                             └─ OCRClient + AiClient (이미지 업로드)

MedicineAlarmScheduler (@Scheduled)
   └─ MedicineAlarmEvent (ApplicationEventPublisher)
         ├─ Push 알림 (PushSubscriptionService)
         └─ TakingLogService.recordTaking → TakingLog 저장

TakingLogMonthlyCalculator
   └─ TakingLog + MedicineSchedule 로 DaySummary/SlotSummary 생성

ReportController
   └─ StatisticsService → PromptManager → AiClient → ReportService
```

- **Event Driven**: 복약 알림은 이벤트를 통해 TakingLog와 Push에 동시에 전달되어 도메인 간 의존을 줄였습니다.
- **AI Pipeline**: 통계 → 프롬프트 → AI 응답 → ReportPayload 로 이어지는 단방향 파이프라인.
- **Planned Slots**: 월간 캘린더는 미래 스케줄을 합성하여 아직 발생하지 않은 복약도 미리 보여줍니다.

---

## 📊 Monitoring & Ops Notes

- `logging.level` 은 기본 INFO, `com.example.onharu` 는 DEBUG 로 설정하여 서버 장애를 즉시 파악합니다 (`application.yaml`).
- Hibernate SQL 로깅 및 슬로우 쿼리 로그(`LOG_QUERIES_SLOWER_THAN_MS`)로 DB 이슈를 추적합니다.
- `.env` 기반 설정으로 로컬/운영 환경을 분리하며, Push/AI/OCR 자격 증명은 환경 변수로 관리합니다.

---

## 🗄 Domain Highlights

| 테이블/엔티티 | 역할 |
| --- | --- |
| `prescriptions`, `medicines`, `medicine_schedules` | 처방전 → 약 → 복약 스케줄 체인, OCR 및 수동 입력 모두 지원 |
| `taking_logs` | 알림 슬롯/복약 기록. 미래 스케줄과 결합해 DayStatus 계산 |
| `reports` | 월간 복약 리포트 JSON을 저장하여 반복 AI 호출을 최소화 |
| `users`, `caregivers`, `auth` 모듈 | 시니어/케어 계정 및 JWT 인증 |
| `push` | VAPID 키 기반 WebPush 구독자 관리 |

---

## 📁 Jump-start Guide

1. **의존성 설치**: `./gradlew dependencies` (필요 시 로컬 Gradle 캐시 준비)
2. **환경 변수**: `.env` 또는 시스템 환경변수에 `DB_URL`, `OPENAI_API_KEY`, `NAVER_OCR_*`, `JWT_SECRET` 등을 설정
3. **DB 준비**: MySQL에 `onharu` 스키마 생성 → `application.yaml` 의 URL/계정 매핑
4. **실행**: `./gradlew bootRun`
5. **스웨거(UI)**: `/swagger-ui` (springdoc starter 기반)
6. **주요 API 확인**: 
   - 처방전: `docs/prescription-api.md`
   - 복약 기록/캘린더: `docs/takinglog-api.md`
   - 리포트 & AI: `docs/report-api.md`, `docs/report-module.md`

---

## 👥 Team

- **Product & Backend**: OnHaru Platform Team (Prescriptions · MedicineSchedule · Reports · Push · OCR)
- **Frontend & Caregiver App**: Companion SPA (not part of this repository)
- **Contact**: `support@onharu.example`

프로젝트나 문서 기여를 원하시면 PR을 열고 `docs/` 폴더에 변경 사항을 기록해주세요.
