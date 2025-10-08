package org.springframework.batch.item.querydsl.reader

import com.querydsl.jpa.impl.JPAQuery
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction

class QuerydslZeroPagingItemReaderTest : FunSpec({

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

    test("QuerydslZeroPagingItemReader should always use offset 0") {
        val firstPageData = listOf(
            TestEntity(id = 1L, name = "test1", value = 100),
            TestEntity(id = 2L, name = "test2", value = 200)
        )
        val secondPageData = listOf(
            TestEntity(id = 3L, name = "test3", value = 300),
            TestEntity(id = 4L, name = "test4", value = 400)
        )

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(0) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returnsMany listOf(firstPageData, secondPageData)

        val reader = QuerydslZeroPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 2,
            queryFunction = { jpaQuery }
        )

        reader.open(mockk(relaxed = true))

        // First page
        reader.read() shouldBe firstPageData[0]
        reader.read() shouldBe firstPageData[1]

        // Second page - should still use offset 0
        reader.read() shouldBe secondPageData[0]
        reader.read() shouldBe secondPageData[1]

        // Verify offset is always 0, never incremented
        verify(exactly = 2) { jpaQuery.offset(0) }
        verify(exactly = 0) { jpaQuery.offset(2) }
    }

    test("QuerydslZeroPagingItemReader should initialize correctly") {
        val reader = QuerydslZeroPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { mockk<JPAQuery<TestEntity>>() }
        )

        reader.pageSize shouldBe 10
    }

    test("QuerydslZeroPagingItemReader should apply correct limit") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(any()) } returns jpaQuery
        every { jpaQuery.limit(5L) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslZeroPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 5,
            queryFunction = { jpaQuery }
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { jpaQuery.limit(5L) }
    }

    test("QuerydslZeroPagingItemReader should handle empty results") {
        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(any()) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns emptyList()

        val reader = QuerydslZeroPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery }
        )

        reader.open(mockk(relaxed = true))
        val result = reader.read()

        result shouldBe null
    }

    test("QuerydslZeroPagingItemReader should use transaction") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(any()) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslZeroPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery }
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { transaction.begin() }
        verify { transaction.commit() }
    }
})
