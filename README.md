# 수강 신청 시스템 (BE-A)

## 프로젝트 개요

크리에이터(강사)가 강의를 개설하고, 클래스메이트(수강생)가 수강 신청 및 결제 확정을 진행하는 백엔드 API 서버입니다.
정원 관리, 상태 전이, 동시성 제어를 핵심 구현 목표로 합니다.

## 기술 스택

- Java 17
- Spring Boot 4.x
- Spring Data JPA
- H2 (in-memory)
- JUnit 5

## 실행 방법

```bash
./gradlew bootRun
```

H2 콘솔: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa` / Password: (없음)

테스트 실행:
```bash
./gradlew test
```

---

## 요구사항 해석 및 가정

### 인증 / 사용자 식별
- 별도 인증 구현 없이 `X-User-Id` 헤더로 userId를 전달하는 방식으로 처리합니다.

### 강의(Class) 상태 전이
- `DRAFT → OPEN → CLOSED` 단방향 전이만 허용합니다.
- 역방향 전이(예: `OPEN → DRAFT`)는 예외 처리합니다.
- `CLOSED`는 신규 신청 불가 상태이며, 기존 확정 수강생의 Enrollment에는 영향을 주지 않습니다.

### 수강 신청(Enrollment) 상태 전이

```
PENDING → CONFIRMED   결제 완료 (외부 PG 콜백을 API 호출로 대체)
PENDING → CANCELLED   결제 전 취소 허용
CONFIRMED → CANCELLED 결제 후 취소, 7일 이내만 허용
```

- 실제 서비스에서는 PG 결제 콜백이 confirm을 트리거하지만, 본 과제에서는 단순 API 호출로 대체합니다.
- 결제 미완료(PENDING) 상태에서도 취소를 허용합니다. 결제 페이지 이탈 등 사용자 흐름을 고려한 결정입니다.
- 한 사용자가 같은 강의에 중복 신청은 불가합니다.

### 정원 관리
- `PENDING` 상태도 정원 카운트에 포함합니다. 결제 시작 시점에 자리를 점유하는 방식입니다.
- 실제 서비스라면 PENDING 만료 처리(예: 30분 TTL)가 필요하지만, 본 과제에서는 미구현 항목으로 남겨둡니다.
- `CANCELLED` 상태는 정원 카운트에서 제외합니다.

---

## 설계 결정과 이유

### 동시성 제어 — 비관적 락 채택
정원 초과 방지를 위해 `SELECT FOR UPDATE` 기반의 비관적 락을 사용합니다.

**낙관적 락을 선택하지 않은 이유:**
- 낙관적 락은 충돌 시 재시도 로직이 필요합니다.
- 수강 신청처럼 마지막 자리에 동시 요청이 몰리는 상황에서는 재시도가 반복되어 오히려 성능이 떨어질 수 있습니다.
- 비관적 락이 구현이 명확하고, 정원 초과 불가라는 비즈니스 규칙을 더 안전하게 보장합니다.

### 패키지 구조 — 레이어드 아키텍처
```
com.example.enrollment
├── domain
│   ├── clazz          # Class 엔티티, 상태, 레포지토리
│   └── enrollment     # Enrollment 엔티티, 상태, 레포지토리
├── application        # 서비스 레이어 (비즈니스 로직)
└── presentation       # 컨트롤러, 요청/응답 DTO

```

### 정원 카운트 쿼리
```sql
SELECT COUNT(*) FROM enrollment
WHERE class_id = ? AND status IN ('PENDING', 'CONFIRMED')
```
CANCELLED를 제외한 활성 신청 수를 기준으로 정원 초과 여부를 판단합니다.

---

## API 목록 및 예시

### 강의(Class)

| Method | URL | 설명 | 헤더 |
|--------|-----|------|------|
| POST | `/classes` | 강의 등록 | X-User-Id |
| GET | `/classes?status=OPEN` | 강의 목록 (상태 필터) | - |
| GET | `/classes/{id}` | 강의 상세 + 현재 신청 인원 | - |
| PATCH | `/classes/{id}/status` | 강의 상태 변경 | X-User-Id |

### 수강 신청(Enrollment)

| Method | URL | 설명 | 헤더 |
|--------|-----|------|------|
| POST | `/classes/{classId}/enrollments` | 수강 신청 (PENDING 생성) | X-User-Id |
| PATCH | `/enrollments/{id}/confirm` | 결제 확정 (CONFIRMED) | X-User-Id |
| PATCH | `/enrollments/{id}/cancel` | 수강 취소 | X-User-Id |
| GET | `/enrollments/me` | 내 수강 신청 목록 | X-User-Id |

## 공통 응답 코드 

| HTTP | code | 설명 |
|------|------|------|
| 409 | ENROLLMENT_CAPACITY_EXCEEDED | 정원 초과 |
| 409 | ENROLLMENT_DUPLICATE | 중복 신청 |
| 400 | INVALID_STATUS_TRANSITION | 잘못된 상태 전이 |
| 403 | UNAUTHORIZED_ACTION | 본인 외 리소스 접근 |


### 요청 / 응답 예시

**강의 등록**
```json
// POST /classes
// Header: X-User-Id: 1
{
  "title": "Spring Boot 완전 정복",
  "description": "JPA부터 배포까지",
  "price": 99000,
  "capacity": 30,
  "startDate": "2025-07-01",
  "endDate": "2025-08-31"
}

// Response 201
{
  "id": 1,
  "title": "Spring Boot 완전 정복",
  "status": "DRAFT",
  "capacity": 30,
  "enrolledCount": 0
}
```

**수강 신청**
```json
// POST /classes/1/enrollments
// Header: X-User-Id: 42

// Response 201
{
  "id": 10,
  "classId": 1,
  "userId": 42,
  "status": "PENDING",
  "createdAt": "2025-06-01T10:00:00"
}
```

**정원 초과 시**
```json
// Response 409
{
  "code": "ENROLLMENT_CAPACITY_EXCEEDED",
  "message": "정원이 초과되었습니다."
}
```


**결제 대기 상태 취소 시**
```json
// PATCH /enrollments/1/cancel
// Header : X-User-Id: 42

// Response 200
{
    "id": 10,
    "classId": 1,
    "userId": 42,
    "status": "CANCELLED",
    "createdAt": "2026-05-22T14:19:57.681729"
}
```
---

## 데이터 모델 설명

### ERD

```
classes
├── id (PK)
├── creator_id (FK → users.id)
├── title
├── description
├── price
├── capacity
├── start_date
├── end_date
├── status (DRAFT / OPEN / CLOSED)
└── created_at

enrollments
├── id (PK)
├── class_id (FK → classes.id)
├── user_id (FK → users.id)
├── status (PENDING / CONFIRMED / CANCELLED)
├── confirmed_at   ← 취소 가능 기간(7일) 계산 기준
└── created_at
```

### 주요 제약
- `(class_id, user_id)` unique 제약 → 중복 신청 방지
- `confirmed_at` 은 CONFIRMED 전이 시점에 기록, 취소 가능 여부 판단에 사용

---

## 테스트 실행 방법

```bash
./gradlew test
```

### 주요 테스트 시나리오

| 분류 | 테스트 내용 |
|------|------------|
| 단위 | Class 상태 전이 규칙 (DRAFT→OPEN→CLOSED, 역방향 예외) |
| 단위 | DRAFT 상태 강의 신청 시 예외 |
| 단위 | 중복 신청 시 예외 |
| 단위 | CONFIRMED 후 7일 초과 취소 시 예외 |
| 통합 | 동시에 30개의 요청을 보내더라도 최종 CONFIRMED/PENDING 합계가 정원을 초과하지 않는지 검증 |
| 통합 | PENDING 상태 취소 후 정원 반환 확인 |

---

## 미구현 / 제약사항

- **PENDING 만료 처리 미구현**: 실제 서비스라면 일정 시간(예: 30분) 후 PENDING 자동 만료가 필요하나, 본 과제 범위에서는 제외합니다.
- **실제 결제 연동 없음**: PG 연동 없이 `/confirm` API 호출로 결제 완료를 대체합니다.
- **인증/인가 미구현**: `X-User-Id` 헤더로 사용자를 식별하며, JWT 등 실제 인증은 미적용입니다.
- **대기열(Waitlist) 미구현**: 선택 구현 항목으로, 시간 관계상 제외합니다.

---

## AI 활용 범위

- 초기 프로젝트 구조 및 보일러플레이트 생성에 AI 도구를 활용했습니다.
- 비관적 락 / 낙관적 락 트레이드오프 검토 시 참고했습니다.
- 테스트 시나리오는 직접 도출했으며, AI 생성 코드는 검토 후 수정하여 반영했습니다.
