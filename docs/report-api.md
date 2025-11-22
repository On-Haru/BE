# Report 도메인 API 명세

`/api/reports` 엔드포인트는 월간 복약 데이터를 기반으로 AI 분석 리포트를 생성/조회합니다. 모든 응답은 `ApiResponse<T>` 형식입니다.

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
    "dateRange": "YYYY.MM.DD ~ YYYY.MM.DD"
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
      { "label": "아침", "rate": number, "status": "GOOD" | "WARN" | "BAD" },
      { "label": "점심", "rate": number, "status": "GOOD" | "WARN" | "BAD" },
      { "label": "저녁", "rate": number, "status": "GOOD" | "WARN" | "BAD" }
    ],
    "medicinePattern": [
      { "medicineName": "string", "rate": number, "aiComment": "string" }
    ]
  }
}
```

### 필드 설명

- **reportMeta**: 리포트 식별 및 기간 정보. `periodType`은 월간(`MONTHLY`) 또는 초기 적응(`ONBOARDING`).
- **aiAnalysis**: AI가 작성한 요약/행동 제안/위험 태그. AI 호출 실패 시 기본 문구로 채워집니다.
- **statistics**: 전체 복약률, 전월 대비 변화(`diff`, `direction`), 평균 지연(분), 미복용 횟수. 데이터가 없으면 `null` 처리.
- **chartData.timePattern**: 아침/점심/저녁 복약률 및 상태 코드(80%↑ GOOD, 50~79 WARN, 이하 BAD).
- **chartData.medicinePattern**: 약물별 복약률과 AI 한 줄 코멘트.

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
      "dateRange": "2025.11.01 ~ 2025.11.30"
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
        { "label": "아침", "rate": 94, "status": "GOOD" },
        { "label": "점심", "rate": 82, "status": "WARN" },
        { "label": "저녁", "rate": 38, "status": "BAD" }
      ],
      "medicinePattern": [
        { "medicineName": "혈압약", "rate": 92, "aiComment": "아침 7시 복약 패턴이 매우 안정적입니다." },
        { "medicineName": "관절약", "rate": 45, "aiComment": "금요일 저녁 복약 누락이 반복됩니다." }
      ]
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

- 스케줄러 없이 **요청 시점**에 리포트를 생성/업데이트합니다.
- AI 응답이 없으면 기본 문구(요약/제안/태그/약별 코멘트)를 사용합니다.
