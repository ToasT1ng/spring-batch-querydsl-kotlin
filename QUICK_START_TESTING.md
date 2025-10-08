# 🚀 빠른 시작: Claude Code로 테스트 유지보수

## 즉시 사용 가능한 명령어

### 1. 새 클래스 테스트 생성
```
/test-for [클래스명]
```

**예시:**
- `/test-for QuerydslCustomReader`
- `/test-for CustomNoOffsetOptions`

### 2. 실패한 테스트 수정
```
/fix-tests
```

테스트 실패 시 이 명령어 하나로 자동 수정됩니다.

### 3. 테스트 커버리지 확인
```
/test-coverage
```

어떤 클래스에 테스트가 없는지 확인합니다.

## 📖 시나리오별 가이드

### 시나리오 1: 새 Reader 클래스 추가

```kotlin
// 1. 새 클래스 작성
class QuerydslCustomReader<T>(...) : QuerydslPagingItemReader<T>(...) {
    // 구현
}

// 2. Claude Code에 요청
```
**Claude에게 입력:**
> QuerydslCustomReader에 대한 테스트 코드를 작성해주세요. QuerydslPagingItemReaderTest를 참고해서 만들어주세요.

**결과:** 
- `QuerydslCustomReaderTest.kt` 파일 생성
- 모든 테스트 케이스 자동 작성
- 테스트 실행 및 검증

---

### 시나리오 2: 기존 코드 수정 후 테스트 실패

```bash
# 1. 테스트 실행
./gradlew :spring-batch-querydsl-kotlin-reader:test

# 2. 실패 확인
```

**Claude에게 입력:**
> /fix-tests

**결과:**
- 실패 원인 분석
- 테스트 자동 수정
- 재실행 및 검증

---

### 시나리오 3: 테스트 커버리지 개선

**Claude에게 입력:**
> /test-coverage

**결과:**
- 테스트 없는 클래스 목록
- 추가 필요한 테스트 제안
- 우선순위 제시

---

### 시나리오 4: 특정 기능 테스트 추가

**Claude에게 입력:**
> QuerydslNoOffsetPagingItemReaderTest에 다음 테스트를 추가해주세요:
> 1. 페이지 크기가 0일 때 에러 처리
> 2. 매우 큰 페이지 사이즈 처리
> 3. 동시성 테스트 (여러 스레드에서 read() 호출)

**결과:**
- 요청한 테스트 케이스 추가
- 기존 코드 스타일 유지
- 테스트 실행 및 검증

---

## 🎯 실전 예제

### 예제 1: Options 클래스 테스트 생성

```
Claude에게: "BaseNoOffsetStringOptions에 대한 테스트를 작성해주세요.
BaseNoOffsetNumberOptionsTest와 동일한 패턴을 사용하되,
String 타입 특성을 고려해주세요."
```

### 예제 2: 복잡한 Mock 설정

```
Claude에게: "QuerydslProjectionReader를 테스트하려고 합니다.
Projection 타입 변환이 포함되어 있어서 mock 설정이 복잡합니다.
어떻게 테스트해야 할까요?"
```

### 예제 3: 기존 테스트 리팩토링

```
Claude에게: "QuerydslPagingItemReaderTest가 너무 길어졌습니다.
중복 코드를 제거하고 헬퍼 함수로 추출해서 리팩토링해주세요."
```

---

## ⚡ 자주 사용하는 패턴

### 패턴 1: "기존 테스트 참고" 패턴
```
"[새클래스]에 대한 테스트를 작성해주세요. 
[기존테스트]를 참고해서 동일한 스타일로 만들어주세요."
```

### 패턴 2: "문제 설명 + 수정 요청" 패턴
```
"[테스트명]가 실패합니다. 
에러: [에러메시지]
원인은 [설명]입니다. 
수정해주세요."
```

### 패턴 3: "점진적 개선" 패턴
```
1. "테스트 커버리지 확인해주세요"
2. "가장 중요한 테스트부터 추가해주세요"
3. "edge case 테스트 추가해주세요"
4. "에러 케이스 테스트 추가해주세요"
```

---

## 🔍 트러블슈팅

### 문제: Mock 설정이 복잡함
**해결:**
```
"[클래스명]의 [메서드명]을 테스트하려는데 mock 설정이 어렵습니다.
어떤 mock이 필요하고 어떻게 설정해야 하나요?
기존 비슷한 테스트가 있으면 참고해주세요."
```

### 문제: 테스트 타임아웃
**해결:**
```
"테스트가 너무 느립니다. 
특히 [테스트명]이 5초 이상 걸립니다.
최적화할 수 있는 방법을 찾아주세요."
```

### 문제: 불안정한 테스트 (Flaky Test)
**해결:**
```
"[테스트명]이 가끔 실패합니다.
타이밍 이슈나 순서 의존성이 있는지 확인하고 수정해주세요."
```

---

## 📋 체크리스트

새 테스트 작성 후 확인사항:

- [ ] Kotest FunSpec 스타일 사용
- [ ] MockK로 모든 의존성 모킹
- [ ] SpringBootTest 미사용
- [ ] beforeTest/afterTest 정리
- [ ] Happy path 테스트 ✓
- [ ] Edge case 테스트 ✓
- [ ] Error handling 테스트 ✓
- [ ] Mock verification 포함
- [ ] 테스트 이름이 명확함
- [ ] 모든 테스트 통과 ✓

---

## 💡 유용한 팁

1. **명확한 컨텍스트 제공**: "이 클래스는 ~를 하는 역할입니다"
2. **기존 코드 참조**: "~Test를 참고해주세요"
3. **구체적인 요구사항**: "~와 ~를 테스트해주세요"
4. **단계별 진행**: 복잡한 테스트는 단계별로 요청

---

## 📚 더 알아보기

- 📖 [TEST_GUIDE.md](./TEST_GUIDE.md) - 상세 가이드
- 📖 [CLAUDE.md](./CLAUDE.md) - 프로젝트 전체 가이드
- 📁 `.claude/test-template.md` - 테스트 템플릿
- 📁 `.claude/commands/` - 사용 가능한 명령어

---

**Happy Testing with Claude Code! 🎉**
