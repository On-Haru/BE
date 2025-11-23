# Report 도메인 API 명세

`/api/reports` 엔드포인트는 월간 복약 데이터를 기반으로 AI 분석 리포트를 생성/조회합니다. 내부적으로 `SpringAiClient`가 Spring AI(OpenAI Chat Completions)를 통해 요약/제안/위험 태그를 생성하며, 모든 응답은 `ApiResponse<T>` 형식입니다.

## 1. 요청

| 항목 | 내용 |
| --- | --- |
| **Method** | `GET` |
| **Path** | `/api/reports` |
| **Query** | `userId` (필수), `year`, `month` (선택, 미지정 시 현재 월) |
| **설명** | 대상 월 리포트가 있으면 반환, 없으면 통계를 계산하고 AI 분석을 수행한 뒤 저장 후 반환 |

## 2. 응답 데이터 (`data`)

```
{
  "reportMeta": {
    "reportId": number | null,
    "title": "string",
    "periodType": "MONTHLY" | "ONBOARDING",
    "dateRange": "YYYY.MM.DD ~ YYYY.MM.DD",
    "userName": "string",
    "userYear": "YYYY"
  },
  "aiAnalysis": {
    "summary": "string",
    "suggestion": "string" | null,
    "riskTags": ["string", ...]
  },
  "statistics": {
    "overallRate": number,
    "comparisonRate": { "diff": number, "direction": "UP" | "DOWN" } | null,
    "averageDelayMinutes": number | null,
    "missedCount": number | null
  },
  "chartData": {
    "timePattern": [
      {
        "label": "아침",
        "rate": number,
        "status": "GOOD" | "WARN" | "BAD",
        "averageDelayMinutes": number | null
      },
      { ... 점심 ... },
      { ... 저녁 ... }
    ],
    "medicinePattern": [
      { "medicineName": "string", "rate": number, "aiComment": "string" }
    ],
    "delayStatistics": {
      "withinFiveMinutesRate": number,
      "overThirtyMinutesRate": number
    }
  }
}
```

### 필드 설명

- **reportMeta**: 리포트 식별 및 기간 정보. `periodType`은 월간(`MONTHLY`) 또는 초기 적응(`ONBOARDING`)이며, `userName/userYear`는 시니어 인적사항 노출용.
- **aiAnalysis**: AI가 작성한 요약/행동 제안/위험 태그. AI 호출 실패 시 기본 문구로 채워집니다.
- **statistics**: 전체 복약률, 전월 대비 변화(`diff`, `direction`), 평균 지연(분), 미복용 횟수. 데이터가 없으면 `null` 처리.
- **chartData.timePattern**: 아침/점심/저녁 복약률 및 상태 코드(80%↑ GOOD, 50~79 WARN, 이하 BAD)와 각 시간대의 평균 복용 지연(`averageDelayMinutes`).
- **chartData.medicinePattern**: 약물별 복약률과 AI 한 줄 코멘트.
- **chartData.delayStatistics**: 알림 후 5분 내 복용 비율과 30분 이상 지연 복용 비율.

## 3. 예시

```http
GET /api/reports?userId=201&year=2025&month=11
```

```json
{
  "success": true,
  "data": {
    "reportMeta": {
      "reportId": 321,
      "title": "김노인 2025년 11월 복약 리포트",
      "periodType": "MONTHLY",
      "dateRange": "2025.11.01 ~ 2025.11.30",
      "userName": "김노인",
      "userYear": "1945"
    },
    "aiAnalysis": {
      "summary": "지난 30일간 전체 복약률은 82%로 안정적이지만, 특히 저녁 복약 누락이 반복되고 있습니다.",
      "suggestion": "저녁 식사 직후 복약 알림을 30분 앞당겨 주세요.",
      "riskTags": ["저녁 복약 주의", "주말 패턴 불안정"]
    },
    "statistics": {
      "overallRate": 82,
      "comparisonRate": { "diff": 6, "direction": "UP" },
      "averageDelayMinutes": 22,
      "missedCount": 14
    },
    "chartData": {
      "timePattern": [
        { "label": "아침", "rate": 94, "status": "GOOD", "averageDelayMinutes": 4 },
        { "label": "점심", "rate": 82, "status": "WARN", "averageDelayMinutes": 15 },
        { "label": "저녁", "rate": 38, "status": "BAD", "averageDelayMinutes": 42 }
      ],
      "medicinePattern": [
        { "medicineName": "혈압약", "rate": 92, "aiComment": "아침 7시 복약 패턴이 매우 안정적입니다." },
        { "medicineName": "관절약", "rate": 45, "aiComment": "금요일 저녁 복약 누락이 반복됩니다." }
      ],
      "delayStatistics": {
        "withinFiveMinutesRate": 56,
        "overThirtyMinutesRate": 21
      }
    }
  },
  "errorCode": null,
  "message": null
}
```

## 4. 오류 코드

| 코드 | 의미 |
| --- | --- |
| `US001` | 사용자 정보를 찾을 수 없음 |
| `RP001` | 리포트 생성 중 내부 오류 발생 |

## 5. 기타

- 스케줄러 없이 **요청 시점**에 리포트를 생성/업데이트합니다. 단, 저장 후 24시간 이상 지난 리포트는 자동으로 재생성하여 최신 통계를 반영합니다.
- `SpringAiClient`는 애플리케이션 부팅 시 `@PostConstruct` 로 health-check 프롬프트를 한 번 전송합니다. 로그의 `Spring AI ChatClient health check succeeded/failed` 메시지로 OpenAI 연결 상태를 확인할 수 있습니다.
- OpenAI 설정은 `spring.ai.openai.*`(API Key, base-url, completions-path, model, temperature, max-tokens) 프로퍼티로 관리합니다. 잘못 구성되거나 네트워크가 차단되면 AI 호출 실패 로그가 남고 아래 기본 문구가 사용됩니다.
- AI 응답이 없으면 기본 문구(요약/제안/태그/약별 코멘트)를 사용합니다.

---

## 처방전 API 변경 사항 (2025-11-23)

### 1. 전체 갱신 업데이트(API)

- **Method / Path**: `PUT /api/prescriptions/{prescriptionId}`
- **Request Body**: 기존 등록과 동일한 `PrescriptionRequest` 스키마(`seniorId`, `issuedDate`, `hospitalName`, `doctorName`, `note`, `medicines[*].schedules[*]`).
- **동작**: 해당 처방전과 연결된 약/스케줄을 모두 삭제한 뒤 새 payload 기준으로 다시 생성하여 완전한 overwrite를 보장합니다. 응답은 `PrescriptionResponse`.
- **예외 처리**: `prescriptionId`가 없으면 `PR001`, `seniorId`가 시니어가 아니면 `CR001`.

### 2. 삭제(API)

- **Method / Path**: `DELETE /api/prescriptions/{prescriptionId}`
- **설명**: 인증 없이 지정된 처방전 및 연관된 약/스케줄을 모두 제거하고 `ApiResponse.success()`를 반환합니다. 존재하지 않는 경우 `PR001`.

### 3. 로깅/Swagger

- `application.yaml`에 `logging.level`을 `root=INFO`, `com.example.onharu=DEBUG`로 조정하여 서버 오류를 콘솔에서 즉시 확인할 수 있습니다.
- Springdoc OpenAPI UI는 `/swagger-ui`에서 확인할 수 있으며, 동일한 요청/응답 스키마를 Swagger 문서로 확인할 수 있습니다.
