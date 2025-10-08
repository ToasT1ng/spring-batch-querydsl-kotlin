package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQuery
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.batch.item.querydsl.reader.TestEntity
import org.springframework.batch.item.querydsl.reader.expression.Expression

class BaseNoOffsetStringOptionsTest : FunSpec({

    test("initKeys should initialize currentId and lastId on first page") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.min()) } returns selectQuery
        every { clonedQuery.select(stringPath.max()) } returns selectQuery
        every { clonedQuery.select(stringPath) } returns selectQuery
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns "aaa" andThen "zzz"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 0)

        options.getCurrentId() shouldBe "aaa"
        options.getLastId() shouldBe "zzz"
    }

    test("initKeys should not initialize on subsequent pages") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 1)

        options.getCurrentId() shouldBe null
        options.getLastId() shouldBe null
    }

    test("createQuery should add where and order by clauses when currentId is set") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.min()) } returns selectQuery
        every { clonedQuery.select(stringPath.max()) } returns selectQuery
        every { clonedQuery.select(stringPath) } returns selectQuery
        every { selectQuery.fetchFirst() } returns "aaa" andThen "zzz"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        every { query.where(any()) } returns query
        every { query.orderBy(any()) } returns query
        every { stringPath.goe(any<String>()) } returns mockk(relaxed = true)
        every { stringPath.loe(any<String>()) } returns mockk(relaxed = true)
        every { stringPath.asc() } returns mockk()

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 0)
        val result = options.createQuery(query, 0)

        result shouldNotBe null
        verify { query.where(any()) }
        verify { query.orderBy(any()) }
    }

    test("createQuery should return query unchanged when currentId is null") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        val result = options.createQuery(query, 0)

        result shouldBe query
        verify(exactly = 0) { query.where(any()) }
    }

    test("where expression should enforce both currentId and lastId boundaries") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.min()) } returns selectQuery
        every { clonedQuery.select(stringPath.max()) } returns selectQuery
        every { clonedQuery.select(stringPath) } returns selectQuery
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns "aaa" andThen "zzz"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        every { query.where(any()) } returns query
        every { query.orderBy(any()) } returns query
        every { stringPath.goe("aaa") } returns mockk(relaxed = true)
        every { stringPath.loe("zzz") } returns mockk(relaxed = true)
        every { stringPath.asc() } returns mockk()

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 0)
        options.createQuery(query, 0)

        // Verify both boundaries are applied
        verify { stringPath.goe("aaa") }
        verify { stringPath.loe("zzz") }
    }

    test("resetCurrentId should update currentId from entity field") {
        val stringPath = mockk<StringPath>()
        val entity = TestEntity(id = 1L, name = "test", value = 100)

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.resetCurrentId(entity)

        options.getCurrentId() shouldBe "test"
    }

    test("initFirstId should use min for ASC expression when not group by") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectMinQuery = mockk<JPAQuery<String>>(relaxed = true)
        val selectMaxQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.min()) } returns selectMinQuery
        every { clonedQuery.select(stringPath.max()) } returns selectMaxQuery
        every { selectMinQuery.fetchFirst() } returns "aaa"
        every { selectMaxQuery.fetchFirst() } returns "zzz"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 0)

        verify { clonedQuery.select(stringPath.min()) }
    }

    test("initFirstId should use max for DESC expression when not group by") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectMinQuery = mockk<JPAQuery<String>>(relaxed = true)
        val selectMaxQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.max()) } returns selectMaxQuery
        every { clonedQuery.select(stringPath.min()) } returns selectMinQuery
        every { selectMaxQuery.fetchFirst() } returns "zzz"
        every { selectMinQuery.fetchFirst() } returns "aaa"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.DESC
        ) {}

        options.initKeys(query, 0)

        verify { clonedQuery.select(stringPath.max()) }
    }

    test("initLastId should use max for ASC expression when not group by") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectMinQuery = mockk<JPAQuery<String>>(relaxed = true)
        val selectMaxQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.min()) } returns selectMinQuery
        every { clonedQuery.select(stringPath.max()) } returns selectMaxQuery
        every { selectMinQuery.fetchFirst() } returns "aaa"
        every { selectMaxQuery.fetchFirst() } returns "zzz"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 0)

        options.getLastId() shouldBe "zzz"
    }

    test("initLastId should use min for DESC expression when not group by") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectMinQuery = mockk<JPAQuery<String>>(relaxed = true)
        val selectMaxQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.max()) } returns selectMaxQuery
        every { clonedQuery.select(stringPath.min()) } returns selectMinQuery
        every { selectMaxQuery.fetchFirst() } returns "zzz"
        every { selectMinQuery.fetchFirst() } returns "aaa"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.DESC
        ) {}

        options.initKeys(query, 0)

        options.getLastId() shouldBe "aaa"
    }

    test("initKeys should use orderBy for GROUP BY queries with ASC") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath) } returns selectQuery
        every { stringPath.asc() } returns mockk()
        every { stringPath.desc() } returns mockk()
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns "group1" andThen "group9"
        every { clonedQuery.toString() } returns "SELECT * FROM test GROUP BY name"

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 0)

        verify(atLeast = 2) { selectQuery.orderBy(any()) }
        options.getCurrentId() shouldBe "group1"
        options.getLastId() shouldBe "group9"
    }

    test("initKeys should use orderBy for GROUP BY queries with DESC") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>()
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath) } returns selectQuery
        every { stringPath.desc() } returns mockk()
        every { stringPath.asc() } returns mockk()
        every { selectQuery.orderBy(any()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns "group9" andThen "group1"
        every { clonedQuery.toString() } returns "SELECT * FROM test GROUP BY name"

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.DESC
        ) {}

        options.initKeys(query, 0)

        verify(atLeast = 2) { selectQuery.orderBy(any()) }
        options.getCurrentId() shouldBe "group9"
        options.getLastId() shouldBe "group1"
    }

    test("where expression should use loe for ASC to enforce lastId boundary") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.min()) } returns selectQuery
        every { clonedQuery.select(stringPath.max()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns "aaa" andThen "zzz"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        every { query.where(any()) } returns query
        every { query.orderBy(any()) } returns query
        every { stringPath.goe(any<String>()) } returns mockk(relaxed = true)
        every { stringPath.loe("zzz") } returns mockk(relaxed = true)
        every { stringPath.asc() } returns mockk()

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.ASC
        ) {}

        options.initKeys(query, 0)
        options.createQuery(query, 0)

        verify { stringPath.loe("zzz") }
    }

    test("where expression should use goe for DESC to enforce lastId boundary") {
        val stringPath = mockk<StringPath>()
        val query = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val clonedQuery = mockk<JPAQuery<TestEntity>>(relaxed = true)
        val selectQuery = mockk<JPAQuery<String>>(relaxed = true)

        every { query.clone() } returns clonedQuery
        every { clonedQuery.select(stringPath.max()) } returns selectQuery
        every { clonedQuery.select(stringPath.min()) } returns selectQuery
        every { selectQuery.fetchFirst() } returns "zzz" andThen "aaa"
        every { clonedQuery.toString() } returns "SELECT * FROM test"

        every { query.where(any()) } returns query
        every { query.orderBy(any()) } returns query
        every { stringPath.loe(any<String>()) } returns mockk(relaxed = true)
        every { stringPath.goe("aaa") } returns mockk(relaxed = true)
        every { stringPath.desc() } returns mockk()

        val options = object : BaseNoOffsetStringOptions<TestEntity>(
            stringPath, "name", Expression.DESC
        ) {}

        options.initKeys(query, 0)
        options.createQuery(query, 0)

        verify { stringPath.goe("aaa") }
    }
})
