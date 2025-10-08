package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQuery
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.batch.item.querydsl.reader.TestEntity
import org.springframework.batch.item.querydsl.reader.expression.Expression

class BaseNoOffsetNumberOptionsTest : FunSpec({

    test("initKeys should initialize currentId and lastId on first page") {
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<Long>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(numberPath.min()) } returns selectQuery
        every { clonedQuery.select(numberPath.max()) } returns selectQuery
        every { clonedQuery.select(numberPath) } returns selectQuery
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns 1L andThen 100L
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.ASC
        ) {}

        options.initKeys(query, 0)

        options.getCurrentId() shouldBe 1L
        options.getLastId() shouldBe 100L
    }

    test("initKeys should not initialize on subsequent pages") {
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>()

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.ASC
        ) {}

        options.initKeys(query, 1)

        options.getCurrentId() shouldBe null
        options.getLastId() shouldBe null
    }

    test("createQuery should add where and order by clauses when currentId is set") {
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<Long>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(numberPath.min()) } returns selectQuery
        every { clonedQuery.select(numberPath.max()) } returns selectQuery
        every { clonedQuery.select(numberPath) } returns selectQuery
        every { selectQuery.fetchFirst() } returns 1L andThen 100L
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        every { query.where(any()) } returns query
        every { query.orderBy(any()) } returns query
        every { numberPath.goe(any<Long>()) } returns mockk(relaxed = true)
        every { numberPath.loe(any<Long>()) } returns mockk(relaxed = true)
        every { numberPath.asc() } returns mockk()

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.ASC
        ) {}

        options.initKeys(query, 0)
        val result = options.createQuery(query, 0)

        result shouldNotBe null
        verify { query.where(any()) }
        verify { query.orderBy(any()) }
    }

    test("createQuery should return query unchanged when currentId is null") {
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>()

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.ASC
        ) {}

        val result = options.createQuery(query, 0)

        result shouldBe query
        verify(exactly = 0) { query.where(any()) }
    }

    test("resetCurrentId should update currentId from entity field") {
        val numberPath = mockk<NumberPath<Long>>()
        val entity = TestEntity(id = 50L, name = "test", value = 100)

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.ASC
        ) {}

        options.resetCurrentId(entity)

        options.getCurrentId() shouldBe 50L
    }

    test("initFirstId should use min for ASC expression when not group by") {
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectMinQuery = mockk<JPAQuery<Long>>(relaxed = true)
        val selectMaxQuery = mockk<JPAQuery<Long>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(numberPath.min()) } returns selectMinQuery
        every { clonedQuery.select(numberPath.max()) } returns selectMaxQuery
        every { selectMinQuery.fetchFirst() } returns 1L
        every { selectMaxQuery.fetchFirst() } returns 100L
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.ASC
        ) {}

        options.initKeys(query, 0)

        verify { clonedQuery.select(numberPath.min()) }
    }

    test("initFirstId should use max for DESC expression when not group by") {
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectMinQuery = mockk<JPAQuery<Long>>(relaxed = true)
        val selectMaxQuery = mockk<JPAQuery<Long>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(numberPath.max()) } returns selectMaxQuery
        every { clonedQuery.select(numberPath.min()) } returns selectMinQuery
        every { selectMaxQuery.fetchFirst() } returns 100L
        every { selectMinQuery.fetchFirst() } returns 1L
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.DESC
        ) {}

        options.initKeys(query, 0)

        verify { clonedQuery.select(numberPath.max()) }
    }

    test("initLastId should use max for ASC expression when not group by") {
        val numberPath = mockk<NumberPath<Long>>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectMinQuery = mockk<JPAQuery<Long>>(relaxed = true)
        val selectMaxQuery = mockk<JPAQuery<Long>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(numberPath.min()) } returns selectMinQuery
        every { clonedQuery.select(numberPath.max()) } returns selectMaxQuery
        every { selectMinQuery.fetchFirst() } returns 1L
        every { selectMaxQuery.fetchFirst() } returns 100L
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetNumberOptions<TestEntity, Long>(
            numberPath, "id", Expression.ASC
        ) {}

        options.initKeys(query, 0)

        options.getLastId() shouldBe 100L
    }
})
