package org.springframework.batch.item.querydsl.reader.options

import com.querydsl.jpa.impl.JPAQuery
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.springframework.batch.item.querydsl.reader.TestEntity
import org.springframework.batch.item.querydsl.reader.expression.Expression

class BaseNoOffsetOptionsTest : FunSpec({

    test("getFieldValue should extract field value from entity") {
        val entity = TestEntity(id = 1L, name = "test", value = 100)

        val options = object : BaseNoOffsetOptions<TestEntity>("name", Expression.ASC) {
            override fun initKeys(query: JPAQuery<TestEntity>, page: Int) {}
            override fun initFirstId(query: JPAQuery<TestEntity>) {}
            override fun initLastId(query: JPAQuery<TestEntity>) {}
            override fun createQuery(query: JPAQuery<TestEntity>, page: Int): JPAQuery<TestEntity> = query
            override fun resetCurrentId(item: TestEntity) {}

            fun testGetFieldValue(item: TestEntity) = getFieldValue(item)
        }

        options.testGetFieldValue(entity) shouldBe "test"
    }

    test("getFieldValue should throw exception for non-existent field") {
        val entity = TestEntity(id = 1L, name = "test", value = 100)

        val options = object : BaseNoOffsetOptions<TestEntity>("nonExistentField", Expression.ASC) {
            override fun initKeys(query: JPAQuery<TestEntity>, page: Int) {}
            override fun initFirstId(query: JPAQuery<TestEntity>) {}
            override fun initLastId(query: JPAQuery<TestEntity>) {}
            override fun createQuery(query: JPAQuery<TestEntity>, page: Int): JPAQuery<TestEntity> = query
            override fun resetCurrentId(item: TestEntity) {}

            fun testGetFieldValue(item: TestEntity) = getFieldValue(item)
        }

        shouldThrow<IllegalArgumentException> {
            options.testGetFieldValue(entity)
        }
    }

    test("isGroupByQuery should detect GROUP BY clause") {
        val options = object : BaseNoOffsetOptions<TestEntity>("name", Expression.ASC) {
            override fun initKeys(query: JPAQuery<TestEntity>, page: Int) {}
            override fun initFirstId(query: JPAQuery<TestEntity>) {}
            override fun initLastId(query: JPAQuery<TestEntity>) {}
            override fun createQuery(query: JPAQuery<TestEntity>, page: Int): JPAQuery<TestEntity> = query
            override fun resetCurrentId(item: TestEntity) {}
        }

        options.isGroupByQuery("SELECT * FROM table GROUP BY id") shouldBe true
        options.isGroupByQuery("SELECT * FROM table") shouldBe false
    }

    test("isGroupByQuery should be case insensitive") {
        val options = object : BaseNoOffsetOptions<TestEntity>("name", Expression.ASC) {
            override fun initKeys(query: JPAQuery<TestEntity>, page: Int) {}
            override fun initFirstId(query: JPAQuery<TestEntity>) {}
            override fun initLastId(query: JPAQuery<TestEntity>) {}
            override fun createQuery(query: JPAQuery<TestEntity>, page: Int): JPAQuery<TestEntity> = query
            override fun resetCurrentId(item: TestEntity) {}
        }

        options.isGroupByQuery("SELECT * FROM table group by id") shouldBe true
        options.isGroupByQuery("SELECT * FROM table GROUP by id") shouldBe true
        options.isGroupByQuery("SELECT * FROM table Group By id") shouldBe true
    }

    test("isGroupByQuery should handle JPAQuery") {
        val query = mockk<JPAQuery<TestEntity>>()
        io.mockk.every { query.toString() } returns "SELECT * FROM table GROUP BY id"

        val options = object : BaseNoOffsetOptions<TestEntity>("name", Expression.ASC) {
            override fun initKeys(query: JPAQuery<TestEntity>, page: Int) {}
            override fun initFirstId(query: JPAQuery<TestEntity>) {}
            override fun initLastId(query: JPAQuery<TestEntity>) {}
            override fun createQuery(query: JPAQuery<TestEntity>, page: Int): JPAQuery<TestEntity> = query
            override fun resetCurrentId(item: TestEntity) {}
        }

        options.isGroupByQuery(query) shouldBe true
    }
})
