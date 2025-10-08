# Test Code Template

This template provides a starting point for writing new test code in this project.

## ItemReader Test Template

```kotlin
package org.springframework.batch.item.querydsl.reader

import com.querydsl.jpa.impl.JPAQuery
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction

class MyNewReaderTest : FunSpec({

    lateinit var entityManagerFactory: EntityManagerFactory
    lateinit var entityManager: EntityManager
    lateinit var transaction: EntityTransaction

    beforeTest {
        entityManagerFactory = mockk()
        entityManager = mockk(relaxed = true)
        transaction = mockk(relaxed = true)

        every { entityManagerFactory.createEntityManager(any<Map<String, Any>>()) } returns entityManager
        every { entityManager.transaction } returns transaction
        every { entityManager.close() } just Runs
        every { transaction.begin() } just Runs
        every { transaction.commit() } just Runs
        every { entityManager.flush() } just Runs
        every { entityManager.clear() } just Runs
    }

    afterTest {
        clearAllMocks()
    }

    test("should initialize correctly") {
        // Arrange
        val reader = MyNewReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { mockk<JPAQuery<TestEntity>>() }
        )

        // Act & Assert
        reader.pageSize shouldBe 10
    }

    test("should read items correctly") {
        // Arrange
        val testData = listOf(
            TestEntity(id = 1L, name = "test1", value = 100)
        )

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = MyNewReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery }
        )

        // Act
        reader.open(mockk(relaxed = true))
        val result = reader.read()

        // Assert
        result shouldBe testData[0]
    }

    test("should handle empty results") {
        // Arrange
        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns emptyList()

        val reader = MyNewReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery }
        )

        // Act
        reader.open(mockk(relaxed = true))
        val result = reader.read()

        // Assert
        result shouldBe null
    }
})
```

## Options Test Template

```kotlin
package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQuery
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.batch.item.querydsl.reader.TestEntity
import org.springframework.batch.item.querydsl.reader.expression.Expression

class MyNewOptionsTest : FunSpec({

    test("should initialize keys on first page") {
        // Arrange
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<Long>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(numberPath.min()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns 1L
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = MyNewOptions(numberPath, "id", Expression.ASC)

        // Act
        options.initKeys(query, 0)

        // Assert
        options.getCurrentId() shouldBe 1L
    }

    test("should create query with where clause") {
        // Arrange
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>(relaxed = true)

        every { query.where(any()) } returns query
        every { query.orderBy(any()) } returns query
        every { numberPath.goe(any<Long>()) } returns mockk(relaxed = true)

        val options = MyNewOptions(numberPath, "id", Expression.ASC)

        // Act
        val result = options.createQuery(query, 0)

        // Assert
        verify { query.where(any()) }
        verify { query.orderBy(any()) }
    }
})
```

## Expression Test Template

```kotlin
package org.springframework.batch.item.querydsl.reader.expression

import com.querydsl.core.types.dsl.NumberPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk

class MyNewExpressionTest : FunSpec({

    test("should create correct expression") {
        // Arrange
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.goe(any<Long>()) } returns mockk()

        // Act
        val result = MyNewExpression.createExpression(numberPath, 0, 1L)

        // Assert
        result.shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }
})
```

## Quick Reference

### Common MockK patterns

```kotlin
// Basic mock
val mock = mockk<MyClass>()

// Relaxed mock (returns defaults for all methods)
val mock = mockk<MyClass>(relaxed = true)

// Mock return value
every { mock.method() } returns value

// Mock multiple return values
every { mock.method() } returnsMany listOf(value1, value2)

// Mock void method
every { mock.method() } just Runs

// Verify method call
verify { mock.method() }

// Verify exact number of calls
verify(exactly = 2) { mock.method() }

// Verify no calls
verify(exactly = 0) { mock.method() }
```

### Common Kotest patterns

```kotlin
// Basic assertion
result shouldBe expected

// Type check
result.shouldBeInstanceOf<MyType>()

// Null checks
result shouldNotBe null
result shouldBe null

// Collection checks
list.size shouldBe 3
list shouldContain item

// Exception testing
shouldThrow<MyException> {
    // code that throws
}
```

### QueryDSL Type Conversion Mock Pattern

```kotlin
// When JPAQuery.select() changes the generic type:
val entityQuery = mockk<JPAQuery<TestEntity>>()
val longQuery = mockk<JPAQuery<Long>>(relaxed = true)

every { entityQuery.select(numberPath.min()) } returns longQuery
every { longQuery.fetchFirst() } returns 1L
```
