package org.springframework.batch.item.querydsl.reader

import com.querydsl.jpa.impl.JPAQuery
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.batch.item.querydsl.reader.options.BaseNoOffsetNumberOptions

class QuerydslNoOffsetIdPagingItemReaderTest : FunSpec({

    lateinit var mockHelper: QuerydslTestMockHelper
    lateinit var options: BaseNoOffsetNumberOptions<TestEntity, Long>

    beforeTest {
        mockHelper = QuerydslTestMockHelper()
        mockHelper.setupEntityManager()

        options = mockk(relaxed = true)
        every { options.initKeys(any(), any()) } just Runs
        every { options.createQuery(any(), any()) } answers { firstArg() }
        every { options.resetCurrentId(any()) } just Runs
    }

    afterTest {
        mockHelper.clearMocks()
    }

    test("QuerydslNoOffsetIdPagingItemReader should initialize options on first page") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { options.initKeys(any(), 0) }
    }

    test("QuerydslNoOffsetIdPagingItemReader should use options to create query") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { options.createQuery(any(), 0) }
    }

    test("QuerydslNoOffsetIdPagingItemReader should reset currentId after reading page") {
        val testData = listOf(
            TestEntity(id = 1L, name = "test1", value = 100),
            TestEntity(id = 2L, name = "test2", value = 200)
        )

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { options.resetCurrentId(testData[1]) }
    }

    test("QuerydslNoOffsetIdPagingItemReader should not reset currentId when results are empty") {
        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns emptyList()

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify(exactly = 0) { options.resetCurrentId(any()) }
    }

    test("QuerydslNoOffsetIdPagingItemReader should apply correct limit") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(5L) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 5,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { jpaQuery.limit(5L) }
    }

    test("QuerydslNoOffsetIdPagingItemReader should handle transacted mode") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { mockHelper.transaction.begin() }
        verify { mockHelper.transaction.commit() }
    }

    test("QuerydslNoOffsetIdPagingItemReader should handle non-transacted mode") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        mockHelper.setupDetach()

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = false,
            options = options
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify(exactly = 0) { mockHelper.transaction.begin() }
        verify(exactly = 0) { mockHelper.transaction.commit() }
        verify { mockHelper.entityManager.detach(testData[0]) }
    }

    test("QuerydslNoOffsetIdPagingItemReader should read multiple pages correctly") {
        val firstPageData = listOf(
            TestEntity(id = 1L, name = "test1", value = 100),
            TestEntity(id = 2L, name = "test2", value = 200)
        )
        val secondPageData = listOf(
            TestEntity(id = 3L, name = "test3", value = 300)
        )

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returnsMany listOf(firstPageData, secondPageData)

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 2,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))

        // First page
        reader.read() shouldBe firstPageData[0]
        reader.read() shouldBe firstPageData[1]

        // Second page
        reader.read() shouldBe secondPageData[0]

        verify { options.initKeys(any(), 0) }
        verify { options.createQuery(any(), 0) }
        verify { options.createQuery(any(), 1) }
        verify { options.resetCurrentId(firstPageData[1]) }
        verify { options.resetCurrentId(secondPageData[0]) }
    }

    test("QuerydslNoOffsetIdPagingItemReader should return null when no more data") {
        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns emptyList()

        val reader = QuerydslNoOffsetIdPagingItemReader(
            entityManagerFactory = mockHelper.entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = true,
            options = options
        )

        reader.open(mockk(relaxed = true))
        val result = reader.read()

        result shouldBe null
    }
})
