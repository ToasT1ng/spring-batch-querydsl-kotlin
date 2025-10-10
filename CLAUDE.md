# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin implementation of Spring Batch with QueryDSL integration, originally based on https://github.com/jojoldu/spring-batch-querydsl. The project provides custom ItemReaders for Spring Batch that leverage QueryDSL for efficient database pagination.

## Project Structure

Multi-module Gradle project with two main modules:

- **spring-batch-querydsl-kotlin-reader**: Core library containing QueryDSL-based ItemReader implementations
- **spring-batch-querydsl-kotlin-sample**: Sample application demonstrating usage with H2 database

## Build Commands

```bash
# Build the entire project
./gradlew build

# Build specific module
./gradlew :spring-batch-querydsl-kotlin-reader:build
./gradlew :spring-batch-querydsl-kotlin-sample:build

# Run tests
./gradlew test

# Run tests for specific module
./gradlew :spring-batch-querydsl-kotlin-reader:test

# Clean build
./gradlew clean build

# Run the sample application (Spring Boot app)
./gradlew :spring-batch-querydsl-kotlin-sample:bootRun
```

## Core Architecture

### ItemReader Implementations

The library provides three main QueryDSL-based ItemReader implementations, all extending `AbstractPagingItemReader`:

1. **QuerydslPagingItemReader**: Standard offset-based pagination
   - Uses traditional `offset()` and `limit()` for pagination
   - Base class for other readers

2. **QuerydslNoOffsetPagingItemReader**: No-offset pagination for better performance
   - Avoids offset overhead by using WHERE clause conditions based on last item's field value
   - Requires `BaseNoOffsetOptions` configuration (Number or String field)
   - Options classes: `QuerydslNoOffsetOptions`, `QuerydslNoOffsetNumberOptions`, `QuerydslNoOffsetStringOptions`

3. **QuerydslNoOffsetIdPagingItemReader**: Specialized no-offset reader for numeric ID fields
   - Similar to NoOffset but specifically typed for Number fields
   - Uses `BaseNoOffsetNumberOptions`

4. **QuerydslZeroPagingItemReader**: Always reads from offset 0
   - Useful for scenarios where data is deleted after processing

### Projection Support

The library supports QueryDSL Projections with dedicated options classes:
- `QuerydslProjectionNoOffsetOptions`
- `QuerydslProjectionNoOffsetNumberOptions`
- `QuerydslProjectionNoOffsetStringOptions`

These handle cases where you're selecting specific fields rather than full entities.

### NoOffset Options Pattern

NoOffset readers use an options pattern to manage pagination state:

- **BaseNoOffsetOptions**: Abstract base class defining the pagination strategy
  - `initKeys()`: Initialize first/last ID on first page
  - `createQuery()`: Add WHERE clause based on current position
  - `resetCurrentId()`: Update current position after reading each page
  - `getFieldValue()`: Reflection-based field value extraction

- Options use `Expression` classes (`WhereExpression`, `OrderExpression`) to define WHERE clause conditions
  - `WhereNumberFunction` and `WhereStringFunction` for type-specific comparisons

### Key Concepts

- **Transacted mode**: Controls whether reads happen in transactions (default: true)
- **EntityManager lifecycle**: Managed by readers (created in `doOpen()`, closed in `doClose()`)
- **Page state tracking**: NoOffset readers track current position via field values instead of offset numbers
- **Group By detection**: Options check for GROUP BY queries to handle them appropriately

## Technology Stack

- Kotlin 1.9.25
- Spring Boot 3.5.5
- Spring Batch
- QueryDSL 5.0.0/5.1.0 (Jakarta)
- JPA with Jakarta persistence
- Java 21
- Gradle with Kotlin DSL
- KAPT for QueryDSL Q-class generation (sample module)

## Important Implementation Notes

- The reader module has `bootJar.enabled = false` and `jar.enabled = true` (it's a library, not an executable)
- Sample module uses KAPT to generate QueryDSL Q-classes
- All readers extend `AbstractPagingItemReader<T>` from Spring Batch
- Entity classes use `allOpen` and `noArg` Kotlin plugins for JPA compatibility
- No-offset pagination improves performance by avoiding large offset values that degrade database query performance

## Git Commit Guidelines

When creating commits for this repository, follow these rules:

1. **Use Korean for commit messages** - Write all commit messages in Korean
2. **Keep messages concise** - Focus on essential information only
   - Avoid test results (e.g., "모든 테스트 통과")
   - Avoid detailed implementation descriptions
   - List only key changes in bullet points
3. **Make small, focused commits** - Each commit should represent a single logical change to make rollbacks easier

Example commit message format:
```
[간결한 제목 - 무엇을 했는지]

- 주요 변경사항 1
- 주요 변경사항 2
- 주요 변경사항 3

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

Use bullet points (-) with line breaks for changes, not comma-separated lists.

## Testing Strategy

### Test Framework
- **Kotest 5.8.0** with FunSpec style for consistent BDD-style tests
- **MockK 1.13.8** for Kotlin-friendly mocking
- **Pure unit tests** - no SpringBootTest to keep tests fast
- **H2 in-memory database** for data-related tests when needed

### Test Structure
```
spring-batch-querydsl-kotlin-reader/src/test/kotlin/
├── org/springframework/batch/item/querydsl/reader/
│   ├── TestEntity.kt                              # Test entity for all tests
│   ├── QuerydslPagingItemReaderTest.kt           # Base paging reader tests
│   ├── QuerydslZeroPagingItemReaderTest.kt       # Zero-offset reader tests
│   ├── QuerydslNoOffsetPagingItemReaderTest.kt   # NoOffset reader tests
│   ├── QuerydslNoOffsetIdPagingItemReaderTest.kt # NoOffset ID reader tests
│   ├── expression/
│   │   ├── ExpressionTest.kt                      # Expression enum tests
│   │   └── WhereExpressionTest.kt                 # WHERE expression tests
│   └── options/
│       ├── BaseNoOffsetOptionsTest.kt             # Base options tests
│       └── BaseNoOffsetNumberOptionsTest.kt       # Number options tests
```

### Writing New Tests

When adding new ItemReader implementations or modifying existing ones:

1. **Use Kotest FunSpec style**:
```kotlin
class MyReaderTest : FunSpec({
    test("should do something") {
        // test code
    }
})
```

2. **Mock dependencies with MockK**:
```kotlin
lateinit var entityManagerFactory: EntityManagerFactory
lateinit var entityManager: EntityManager

beforeTest {
    entityManagerFactory = mockk()
    entityManager = mockk(relaxed = true)
    every { entityManagerFactory.createEntityManager(any()) } returns entityManager
}

afterTest {
    clearAllMocks()
}
```

3. **Test naming convention**: Use descriptive names like "should [expected behavior] when [condition]"

4. **Coverage requirements**:
   - Test happy path scenarios
   - Test edge cases (empty results, null values)
   - Test transacted vs non-transacted modes
   - Test pagination across multiple pages
   - Verify mock interactions with `verify { ... }`

5. **Type-specific mocking for QueryDSL**:
```kotlin
// When testing JPAQuery.select() that changes type:
val query = mockk<JPAQuery<TestEntity>>()
val selectQuery = mockk<JPAQuery<Long>>()
every { query.select(numberPath.min()) } returns selectQuery
every { selectQuery.fetchFirst() } returns 1L
```

### Running Tests

```bash
# Run all reader module tests
./gradlew :spring-batch-querydsl-kotlin-reader:test

# Run specific test class
./gradlew :spring-batch-querydsl-kotlin-reader:test --tests "QuerydslPagingItemReaderTest"

# Run with test report
./gradlew :spring-batch-querydsl-kotlin-reader:test
# Open: build/reports/tests/test/index.html
```

### Test Maintenance Workflow

When modifying code that requires test updates:

1. Run existing tests to identify failures
2. Update test mocks/expectations to match new behavior
3. Add new test cases for new functionality
4. Ensure all tests pass before committing
5. Keep test coverage at 100% for critical paths

### Common Testing Patterns

**Testing pagination logic**:
```kotlin
test("should paginate correctly") {
    val firstPage = listOf(entity1, entity2)
    val secondPage = listOf(entity3)

    every { query.fetch() } returnsMany listOf(firstPage, secondPage)

    reader.read() shouldBe entity1
    reader.read() shouldBe entity2
    reader.read() shouldBe entity3
}
```

**Testing Options initialization**:
```kotlin
test("should initialize keys on first page") {
    val selectQuery = mockk<JPAQuery<Long>>(relaxed = true)
    every { clonedQuery.select(field.min()) } returns selectQuery
    every { selectQuery.fetchFirst() } returns 1L

    options.initKeys(query, 0)
    options.getCurrentId() shouldBe 1L
}
```
