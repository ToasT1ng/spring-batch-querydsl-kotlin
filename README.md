# Spring Batch QueryDSL Kotlin

Kotlin implementation of Spring Batch with QueryDSL integration for efficient database pagination.

## Original Source

This project is based on [jojoldu/spring-batch-querydsl](https://github.com/jojoldu/spring-batch-querydsl)

## Overview

This library provides custom ItemReaders for Spring Batch that leverage QueryDSL for efficient database pagination. It includes various pagination strategies optimized for different use cases.

## Features

### ItemReader Implementations

1. **QuerydslPagingItemReader**
   - Standard offset-based pagination using `offset()` and `limit()`
   - Base class for other readers

2. **QuerydslNoOffsetPagingItemReader**
   - No-offset pagination for better performance
   - Avoids offset overhead by using WHERE clause conditions
   - Supports both Number and String fields

3. **QuerydslNoOffsetIdPagingItemReader**
   - Specialized no-offset reader for numeric ID fields
   - Optimized for common ID-based pagination scenarios

4. **QuerydslZeroPagingItemReader**
   - Always reads from offset 0
   - Useful for scenarios where data is deleted after processing

### Pagination Options

- **Number-based pagination**: `QuerydslNoOffsetNumberOptions`, `QuerydslProjectionNoOffsetNumberOptions`
- **String-based pagination**: `QuerydslNoOffsetStringOptions`, `QuerydslProjectionNoOffsetStringOptions`
- **Projection support**: For selecting specific fields rather than full entities

## Technology Stack

- Kotlin 1.9.25
- Spring Boot 3.5.5
- Spring Batch
- QueryDSL 5.0.0/5.1.0 (Jakarta)
- JPA with Jakarta persistence
- Java 21
- Gradle with Kotlin DSL

## Project Structure

```
spring-batch-querydsl-kotlin/
├── spring-batch-querydsl-kotlin-reader/    # Core library
│   ├── src/main/kotlin/
│   │   └── org/springframework/batch/item/querydsl/reader/
│   │       ├── QuerydslPagingItemReader.kt
│   │       ├── QuerydslNoOffsetPagingItemReader.kt
│   │       ├── QuerydslNoOffsetIdPagingItemReader.kt
│   │       ├── QuerydslZeroPagingItemReader.kt
│   │       ├── expression/
│   │       └── options/
│   └── src/test/kotlin/                    # Unit tests
└── spring-batch-querydsl-kotlin-sample/    # Sample application
    └── src/main/kotlin/
```

## Build & Run

```bash
# Build the entire project
./gradlew build

# Run tests
./gradlew test

# Run the sample application
./gradlew :spring-batch-querydsl-kotlin-sample:bootRun
```

## Testing

This project uses:
- **Kotest 5.8.0** for BDD-style testing
- **MockK 1.13.8** for Kotlin-friendly mocking
- **H2** in-memory database for testing

For detailed testing guidelines, see [TEST_GUIDE.md](TEST_GUIDE.md)

## Usage Example

```kotlin
@Bean
fun itemReader(
    entityManagerFactory: EntityManagerFactory
): QuerydslNoOffsetPagingItemReader<YourEntity> {
    val options = QuerydslNoOffsetNumberOptions(
        QYourEntity.yourEntity.id,
        Expression.ASC
    )

    return QuerydslNoOffsetPagingItemReader(
        name = "yourEntityReader",
        entityManagerFactory = entityManagerFactory,
        pageSize = 100,
        querySupplier = {
            JPAQueryFactory(it)
                .selectFrom(QYourEntity.yourEntity)
        },
        options = options
    )
}
```

## Why No-Offset Pagination?

Traditional offset-based pagination can suffer from performance degradation as the offset value increases. No-offset pagination uses WHERE clause conditions based on the last item's field value, providing consistent performance regardless of the page number.

## License

This project follows the same license as the original source project.
