package org.springframework.batch.item.querydsl.reader

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityTransaction

class QuerydslPagingItemReaderTest : FunSpec({

    lateinit var entityManagerFactory: EntityManagerFactory
    lateinit var entityManager: EntityManager
    lateinit var transaction: EntityTransaction
    lateinit var query: JPAQuery<TestEntity>

    beforeTest {
        entityManagerFactory = mockk()
        entityManager = mockk(relaxed = true)
        transaction = mockk(relaxed = true)
        query = mockk()

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

    test("QuerydslPagingItemReader should initialize with correct page size") {
        val pageSize = 10
        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = pageSize,
            queryFunction = { mockk<JPAQuery<TestEntity>>() }
        )

        reader.pageSize shouldBe pageSize
    }

    test("QuerydslPagingItemReader should create entity manager on open") {
        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { mockk<JPAQuery<TestEntity>>() }
        )

        reader.open(mockk(relaxed = true))

        verify { entityManagerFactory.createEntityManager(any<Map<String, Any>>()) }
    }

    test("QuerydslPagingItemReader should read page with offset and limit") {
        val testData = listOf(
            TestEntity(id = 1L, name = "test1", value = 100),
            TestEntity(id = 2L, name = "test2", value = 200)
        )

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(any()) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery }
        )

        reader.open(mockk(relaxed = true))
        val firstItem = reader.read()
        val secondItem = reader.read()

        firstItem shouldBe testData[0]
        secondItem shouldBe testData[1]

        verify { jpaQuery.offset(0) }
        verify { jpaQuery.limit(10) }
        verify { jpaQuery.fetch() }
    }

    test("QuerydslPagingItemReader should handle transacted mode") {
        val testData = listOf(TestEntity(id = 1L, name = "test1", value = 100))

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(any()) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData

        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = true
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify { transaction.begin() }
        verify { transaction.commit() }
        verify { entityManager.flush() }
        verify { entityManager.clear() }
    }

    test("QuerydslPagingItemReader should handle non-transacted mode") {
        val testEntity = TestEntity(id = 1L, name = "test1", value = 100)
        val testData = listOf(testEntity)

        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(any()) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns testData
        every { entityManager.detach(any()) } just Runs

        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery },
            transacted = false
        )

        reader.open(mockk(relaxed = true))
        reader.read()

        verify(exactly = 0) { transaction.begin() }
        verify(exactly = 0) { transaction.commit() }
        verify { entityManager.detach(testEntity) }
    }

    test("QuerydslPagingItemReader should return null when no more data") {
        val jpaQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        every { jpaQuery.offset(any()) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returns emptyList()

        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { jpaQuery }
        )

        reader.open(mockk(relaxed = true))
        val result = reader.read()

        result shouldBe null
    }

    test("QuerydslPagingItemReader should close entity manager on close") {
        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 10,
            queryFunction = { mockk<JPAQuery<TestEntity>>() }
        )

        reader.open(mockk(relaxed = true))
        reader.close()

        verify { entityManager.close() }
    }

    test("QuerydslPagingItemReader should paginate correctly on second page") {
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
        every { jpaQuery.offset(2) } returns jpaQuery
        every { jpaQuery.limit(any()) } returns jpaQuery
        every { jpaQuery.fetch() } returnsMany listOf(firstPageData, secondPageData)

        val reader = QuerydslPagingItemReader(
            entityManagerFactory = entityManagerFactory,
            pageSize = 2,
            queryFunction = { jpaQuery }
        )

        reader.open(mockk(relaxed = true))

        // First page
        reader.read() shouldBe firstPageData[0]
        reader.read() shouldBe firstPageData[1]

        // Second page
        reader.read() shouldBe secondPageData[0]
        reader.read() shouldBe secondPageData[1]

        verify { jpaQuery.offset(0) }
        verify { jpaQuery.offset(2) }
    }
})
