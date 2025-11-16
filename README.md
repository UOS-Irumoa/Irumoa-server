# Irumoa-server

서울시립대 학생들을 위한 비교과/프로그램 공지 수집 및 추천 백엔드입니다. 공지를 다양한 조건으로 검색하고, 사용자의 클릭 로그를 적재하여 추후 추천 모델에 활용할 수 있도록 설계돼 있습니다.

## 기술 스택
- Java 17, Gradle
- Spring Boot 3.5.7 (Web, Validation)
- Spring Data JPA (Hibernate)
- MySQL 8.x
- Flyway DB 마이그레이션

## 실행 방법
1. `.env` 파일을 생성해 다음 항목을 채웁니다.
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://<HOST>:3306/uoscholar_db
   SPRING_DATASOURCE_USERNAME=<USER>
   SPRING_DATASOURCE_PASSWORD=<PASSWORD>
   ```
2. 의존성을 내려받고 애플리케이션을 실행합니다.
   ```bash
   ./gradlew bootRun
   ```
3. 기본 포트 `8080`에서 API를 호출할 수 있습니다.

## API 개요
모든 Notice 관련 API는 `/notices` 하위 경로에 존재합니다.

### GET `/notices/search`
공지 목록을 조건 검색합니다. `@ModelAttribute` 바인딩이므로 동일한 파라미터를 여러 번 넘기면 배열로 처리됩니다. 예: `?department=도시사회학과&department=경영학부`

| 파라미터 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| `department` | string[] | 선택 | 관심 학과. 값이 있으면 공지의 학과가 해당 목록 또는 `제한없음` 이어야 합니다. |
| `grade` | int | 선택 | 학년. `filter=true`일 때만 적용. 0 이상. |
| `filter` | boolean | 선택 | `true`면 학년 필터 활성화, 그 외에는 학년 무시. |
| `interests` | string[] | 선택 | 관심 분야(카테고리) 목록. |
| `category` | string[] | 선택 | 공지 분류 목록. |
| `keyword` | string | 선택 | 제목/내용/학과에 대한 부분 일치 검색. 값의 공백은 자동으로 trim. |
| `state` | string | 선택 | 모집 상태. `모집예정`, `모집중`, `모집완료` 중 하나. |
| `page` | int | 선택 | 기본 0. 0 이상. |
| `size` | int | 선택 | 기본 15. 1 이상. |

#### 응답 예시
```json
{
  "content": [
    {
      "id": 1,
      "title": "미리보기 제목",
      "link": "https://...",
      "content": "본문 요약",
      "appStartDate": "2024-11-01",
      "appEndDate": "2024-11-30",
      "categories": ["공모전", "취업"],
      "departments": ["경영학부", "제한없음"],
      "grades": [2, 3, 0]
    }
  ],
  "page": 0,
  "size": 15,
  "totalElements": 27,
  "totalPages": 2
}
```

### POST `/notices/click`
사용자가 특정 공지 링크를 클릭했을 때 로그를 적재합니다. 응답은 본문 없이 `201 Created`입니다.

#### 요청 바디
```json
{
  "id": 123,
  "department": ["경영학부", "도시사회학과"],
  "grade": 3,
  "interests": ["AI", "데이터"]
}
```

- `id`: 공지 식별자 (`program.id`)
- `department`: 사용자의 학과 또는 관심 학과 리스트
- `grade`: 사용자의 학년
- `interests`: 관심 분야 리스트

해당 정보는 `action_log` 테이블에 JSON 문자열 형태로 저장되며, 추후 추천/통계에 활용할 수 있습니다.
