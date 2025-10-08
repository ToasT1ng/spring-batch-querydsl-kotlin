# Test Maintenance Guide

이 문서는 Claude Code를 사용하여 테스트 코드를 유지보수하는 방법을 설명합니다.

## 📋 준비사항

1. Claude Code 설치 및 설정
2. 프로젝트의 CLAUDE.md 파일 읽기 (테스트 전략 섹션 참고)
3. `.claude/test-template.md` 파일 참고

## 🎯 Claude Code로 테스트 유지보수하기

### 1. 새 클래스에 대한 테스트 생성

```bash
# Slash command 사용
/test-for QuerydslNewReader

# 또는 직접 요청
"QuerydslNewReader 클래스에 대한 테스트 코드를 작성해주세요"
```

Claude Code가 자동으로:
- 클래스를 분석하고
- 테스트 파일을 생성하고
- Kotest + MockK 패턴을 적용하고
- 테스트를 실행하여 검증합니다

### 2. 코드 변경 후 테스트 수정

```bash
# Slash command 사용
/fix-tests

# 또는 직접 요청
"테스트가 실패했습니다. 고쳐주세요"
```

Claude Code가:
- 실패한 테스트를 확인하고
- 변경된 코드를 분석하고
- 테스트를 수정합니다

### 3. 테스트 커버리지 확인

```bash
# Slash command 사용
/test-coverage

# 또는 직접 요청
"reader 모듈의 테스트 커버리지를 확인해주세요"
```

### 4. 기존 테스트 개선

```text
"QuerydslPagingItemReaderTest에 edge case 테스트를 추가해주세요"
"트랜잭션 모드 테스트가 부족한 것 같은데 추가해주세요"
```

## 📝 테스트 작성 원칙

### 필수 사항
- ✅ Kotest FunSpec 스타일 사용
- ✅ MockK를 사용한 모킹
- ✅ SpringBootTest 사용하지 않기
- ✅ 테스트 이름: "should [행동] when [조건]" 패턴
- ✅ beforeTest/afterTest로 setup/cleanup

### 테스트 범위
각 클래스마다 다음을 테스트:
- ✅ Happy path (정상 동작)
- ✅ Edge cases (빈 결과, null, 경계값)
- ✅ Error handling (예외 상황)
- ✅ Mock verification (중요한 상호작용)

## 🔄 일반적인 시나리오

### 새 ItemReader 추가 시

1. 구현 클래스 작성
2. Claude에게 요청:
   ```
   "새로 만든 QuerydslCustomReader에 대한 테스트를 작성해주세요.
   기존 QuerydslPagingItemReaderTest를 참고해서 만들어주세요."
   ```

### Options 클래스 추가 시

1. Options 클래스 작성
2. Claude에게 요청:
   ```
   "CustomNoOffsetOptions에 대한 테스트를 작성해주세요.
   BaseNoOffsetNumberOptionsTest 패턴을 따라주세요."
   ```

### 기존 코드 수정 시

1. 코드 수정
2. 테스트 실행: `./gradlew :spring-batch-querydsl-kotlin-reader:test`
3. 실패 시 Claude에게 요청:
   ```
   "테스트가 실패합니다. 에러 메시지:
   [에러 메시지 붙여넣기]

   코드를 이렇게 수정했습니다:
   [변경 사항 설명]

   테스트를 수정해주세요."
   ```

## 🎨 템플릿 활용

`.claude/test-template.md`에 다음 템플릿이 있습니다:
- ItemReader 테스트 템플릿
- Options 테스트 템플릿
- Expression 테스트 템플릿
- MockK 패턴 모음
- Kotest 패턴 모음

새 테스트 작성 시 이 템플릿을 참고하도록 Claude에게 요청하세요:
```
"test-template.md를 참고해서 새 테스트를 작성해주세요"
```

## 🚀 빠른 커맨드 참고

```bash
# 테스트 실행
./gradlew :spring-batch-querydsl-kotlin-reader:test

# 특정 테스트만 실행
./gradlew :spring-batch-querydsl-kotlin-reader:test --tests "QuerydslPagingItemReaderTest"

# 테스트 리포트 확인
open build/reports/tests/test/index.html

# 실패한 테스트만 재실행
./gradlew :spring-batch-querydsl-kotlin-reader:test --rerun-tasks
```

## 💡 팁

### 효과적인 프롬프트 작성

**좋은 예시:**
```
"QuerydslProjectionReader에 대한 테스트를 작성해주세요.
- Kotest FunSpec 사용
- MockK로 모킹
- 페이지네이션 테스트 포함
- Projection 타입 변환 테스트 포함
- 기존 QuerydslNoOffsetPagingItemReaderTest 패턴 참고"
```

**나쁜 예시:**
```
"테스트 만들어줘"  # 너무 모호함
```

### 복잡한 Mock 설정이 필요한 경우

QueryDSL처럼 타입이 변경되는 경우 명시적으로 안내:
```
"JPAQuery<TestEntity>가 select()로 JPAQuery<Long>으로 바뀌는 부분이 있습니다.
각각 별도의 mock을 만들어서 처리해주세요."
```

### 기존 테스트 개선

```
"QuerydslPagingItemReaderTest를 검토하고
빠진 테스트 케이스가 있으면 추가해주세요.
특히 에러 케이스와 경계값 테스트를 확인해주세요."
```

## 📚 참고 자료

- `CLAUDE.md` - 전체 프로젝트 가이드
- `.claude/test-template.md` - 테스트 코드 템플릿
- `.claude/commands/` - Slash command 정의
- 기존 테스트 코드 - 실제 예시 참고

## ❓ 자주 묻는 질문

**Q: SpringBootTest를 사용하지 않는 이유는?**
A: 빠른 테스트 실행을 위해 순수 단위 테스트를 사용합니다. MockK로 모든 의존성을 모킹합니다.

**Q: 통합 테스트는 어디서 작성하나요?**
A: Sample 모듈에서 실제 DB와 함께 통합 테스트를 작성합니다.

**Q: 테스트가 너무 복잡해지면?**
A: Claude에게 "이 테스트를 더 간단하게 리팩토링해주세요"라고 요청하세요.

**Q: Mock 설정이 어려운 경우?**
A: 기존 유사한 테스트를 찾아서 "이 패턴을 따라서 만들어주세요"라고 요청하세요.
