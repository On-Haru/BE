# Report & Statistics Module Overview

월간 복약 데이터를 분석해 AI 리포트를 생성/저장/서빙하는 구성요소를 정리합니다.

## 1. 전체 플로우

```
TakingLog → StatisticsService (통계 계산)
          → PromptManager   (프롬프트 생성)
          → AiClient        (AI 분석 호출)
          → ReportService   (저장/조회)
          → ReportController (API 응답)
```

- `StatisticsService.buildPayload(User, YearMonth)`
  - 지정 월의 TakingLog를 집계하여 **ReportPayload 기본 값**(메타/통계/차트)을 생성합니다.
- `PromptManager.buildPrompt(ReportPayload, YearMonth)`
  - 기본 통계를 요약한 프롬프트 문자열을 만들어 AI에게 전달할 입력을 구성합니다.
- `AiClient.generate(AiRequest)`
  - WebClient 기반 실제 AI 호출 또는 Mock 구현. 요약/제안/위험 태그/약별 코멘트를 수신합니다.
- `ReportService`
  - 월별 리포트 존재 여부 확인 → 없으면 새로 생성 후 `Report` 엔티티(JSON) 저장 → 결과 반환.
- `ReportController`
  - `GET /api/reports` 요청 시 `userId`, `year`, `month` 기준으로 저장된 리포트 반환(없으면 즉시 생성).

## 2. ReportPayload 구조

코드 위치: `report/application/dto/ReportPayload.java`

```
ReportPayload
├─ reportMeta (reportId, title, periodType, dateRange)
├─ aiAnalysis (summary, suggestion, riskTags[])
├─ statistics (overallRate, comparisonRate{diff,direction}, averageDelayMinutes, missedCount)
└─ chartData
    ├─ timePattern[]  (label=아침/점심/저녁, rate, status=GOOD|WARN|BAD)
    └─ medicinePattern[] (medicineName, rate, aiComment)
```

- `ReportPayload.createBase(...)` : 통계 계산 직후, AI 분석 전 상태를 생성.
- `withAiInsights(...)` : AI 응답(요약/제안/위험태그/약별 코멘트)을 반영.
- `withReportId(Long)` : 저장된 엔티티 ID를 메타 정보에 주입.

## 3. API 명세 (간단 버전)

- **Endpoint**: `GET /api/reports`
- **Query**: `userId` (필수), `year` (선택), `month` (선택, 기본 현재 월)
- **동작**: 해당 월 리포트를 조회. 없으면 `StatisticsService` → `AiClient` 순으로 생성 후 반환.
- **Response (`data`)**: 위 `ReportPayload` 구조 그대로 리턴.

응답 예시는 `docs/feat-report.md`의 JSON 스키마 참조.

## 4. 설정

- `ai.client.*` (`application.yaml`)
  - `enabled`: true → WebClientAiClient, false → MockAiClient
  - `base-url`, `api-key`, `timeout`
- `scheduler.*` 항목은 복약 알림 용도로만 사용하며, 리포트 생성에는 별도 스케줄러가 없습니다(요청 들어올 때 생성/조회).

## 5. 확장 포인트

- **AI 공급자 교체**: `AiClient` 구현만 바꾸면 됨.
- **데이터 필드 추가**: `ReportPayload`에 신규 필드/서브레코드 추가 → `StatisticsService`와 `PromptManager`에서 값 채움.
- **저장 전략 변경**: 현재는 JSON 문자열로 저장. 필요 시 JSON 컬럼(@JdbcTypeCode) 또는 서브테이블로 분리 가능.

이 문서를 기반으로 각 컴포넌트의 역할과 데이터 흐름을 빠르게 파악할 수 있습니다.
