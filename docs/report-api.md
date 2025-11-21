# Report 도메인 API 명세

본 문서는 `/api/reports` 하위 리포트·통계 관련 REST API를 설명합니다. 모든 응답은 공통 래퍼 `ApiResponse<T>(success, data, errorCode, message)` 형식을 사용합니다.

## 공통 응답 구조
| 필드 | 타입 | 설명 |
| --- | --- | --- |
| success | boolean | 처리 성공 여부 |
| data | object \| null | API별 응답 페이로드 |
| errorCode | string \| null | 비즈니스 오류 코드 (예: `RP001`) |
| message | string \| null | 오류 혹은 예외 메시지 |

## 1. 최근 복약 리포트 조회 (AI 분석 포함)
| 항목 | 내용 |
| --- | --- |
| Endpoint | `GET /api/reports/latest` |
| 설명 | 특정 시니어의 최신 복약 데이터를 기반으로 통계와 AI 분석 결과를 반환합니다. 데이터 양에 따라 ONBOARDING ↔ MONTHLY 모드가 자동 전환됩니다. |
| 인증 | 필요 (`Authorization: Bearer <token>`) |

### 요청 파라미터 (Query String)
| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| seniorId | long | ✅ | 조회 대상 시니어 PK |

### 응답 데이터 개요 (`data`)
| 필드 | 타입 | 설명 |
| --- | --- | --- |
| reportMeta | object | 리포트 메타 정보 (기간, 타입 등) |
| aiAnalysis | object | AI 생성 텍스트 분석 및 제안 |
| statistics | object | 수치 데이터 (복약률, 지연 시간 등) |
| chartData | object | 시각화용 상세 데이터 |

#### reportMeta
| 필드 | 타입 | 설명 |
| --- | --- | --- |
| reportId | long | 리포트 고유 ID |
| title | string | 리포트 제목 (예: `김노인 님의 11월 월간 리포트`) |
| periodType | string | `ONBOARDING` 또는 `MONTHLY` |
| dateRange | string | 분석 기간 (예: `2025.10.22 ~ 2025.11.21`) |

#### aiAnalysis
| 필드 | 타입 | 설명 |
| --- | --- | --- |
| summary | string | AI 한 줄 요약 및 총평 |
| suggestion | string | AI 행동 제안 (알림 조정 등) |
| riskTags | list | 주의 태그 (예: `["저녁 복약 주의"]`) |

#### statistics
| 필드 | 타입 | 설명 |
| --- | --- | --- |
| overallRate | integer | 전체 복약 이행률 (0~100) |
| comparisonRate | object \| null | 지난달 대비 비교 데이터 |
| └ diff | integer | 변화량 절댓값 |
| └ direction | string | `UP`, `DOWN`, `SAME` |
| averageDelayMinutes | integer | 알림 후 평균 지연 시간(분) |
| missedCount | integer | 미복용 횟수 |

#### chartData
| 필드 | 타입 | 설명 |
| --- | --- | --- |
| timePattern | list | 시간대별(아침/점심/저녁) 복약 현황 |
| └ label | string | `아침`, `점심`, `저녁` |
| └ rate | integer | 해당 시간대 복약률 |
| └ status | string | `GOOD`, `WARN`, `BAD`, `NONE` |
| medicinePattern | list | 약물별 상세 현황 |
| └ medicineName | string | 약 이름 |
| └ rate | integer | 약별 복약률 |
| └ aiComment | string \| null | 약별 AI 코멘트 |

### 요청·응답 예시
#### 데이터 충분 (MONTHLY)
```http
GET /api/reports/latest?seniorId=101
Authorization: Bearer eyJhbGciOiJIUzI1NiIsIn...
```

```json
{
  "success": true,
  "data": {
    "reportMeta": {
      "reportId": 1055,
      "title": "김노인 님의 11월 월간 리포트",
      "periodType": "MONTHLY",
      "dateRange": "2025.10.22 ~ 2025.11.21"
    },
    "aiAnalysis": {
      "summary": "지난 한 달간 전체 복약률은 82%로 비교적 안정적이지만, 특히 저녁 시간대에 복약 누락이 자주 발생하고 있습니다.",
      "suggestion": "저녁 식사 직후로 알림 시간을 30분 당기거나, 보호자가 저녁 시간에 한 번 더 확인해 주시는 것을 권장합니다.",
      "riskTags": [
        "저녁 복약 주의",
        "주말 패턴 불안정"
      ]
    },
    "statistics": {
      "overallRate": 82,
      "comparisonRate": {
        "diff": 6,
        "direction": "UP"
      },
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
        {
          "medicineName": "혈압약",
          "rate": 92,
          "aiComment": "전반적으로 안정적인 복약 패턴입니다."
        },
        {
          "medicineName": "관절약",
          "rate": 60,
          "aiComment": "저녁에 주로 드시는 약이라 누락이 잦습니다."
        }
      ]
    }
  },
  "errorCode": null,
  "message": null
}
```

#### 신규 유저 (ONBOARDING)
```json
{
  "success": true,
  "data": {
    "reportMeta": {
      "periodType": "ONBOARDING",
      "title": "김노인 님의 복약 리포트"
    },
    "aiAnalysis": {
      "summary": "김노인 님, 복약 관리를 시작하신 지 3일째네요! 100% 복약률로 아주 훌륭한 출발입니다.",
      "riskTags": []
    },
    "statistics": {
      "overallRate": 100,
      "comparisonRate": null
    }
  },
  "errorCode": null,
  "message": null
}
```

### 오류 코드
| 코드 | HTTP | 메시지 | 발생 조건 |
| --- | --- | --- | --- |
| US001 | 404 | 유저가 존재하지 않습니다. | 잘못된 `seniorId` 요청 |
| RP001 | 500 | 리포트 생성 중 오류가 발생했습니다. | AI 서비스 지연 또는 DB 오류 |
| AU002 | 403 | 해당 시니어 정보에 접근 권한이 없습니다. | 매칭되지 않은 보호자 접근 |

