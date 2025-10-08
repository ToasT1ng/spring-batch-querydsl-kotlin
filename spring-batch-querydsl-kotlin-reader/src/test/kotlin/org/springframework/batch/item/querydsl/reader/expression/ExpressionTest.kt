package org.springframework.batch.item.querydsl.reader.expression

import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk

class ExpressionTest : FunSpec({

    test("ASC expression should have GT where and ASC order") {
        Expression.ASC.where shouldBe WhereExpression.GT
        Expression.ASC.order shouldBe OrderExpression.ASC
    }

    test("DESC expression should have LT where and DESC order") {
        Expression.DESC.where shouldBe WhereExpression.LT
        Expression.DESC.order shouldBe OrderExpression.DESC
    }

    test("isAsc should return true for ASC expression") {
        Expression.ASC.isAsc() shouldBe true
    }

    test("isAsc should return false for DESC expression") {
        Expression.DESC.isAsc() shouldBe false
    }

    test("ASC order for StringPath should create ascending order") {
        val stringPath = mockk<StringPath>()
        every { stringPath.asc() } returns mockk()

        Expression.ASC.order(stringPath)
            .shouldBeInstanceOf<com.querydsl.core.types.OrderSpecifier<*>>()
    }

    test("DESC order for StringPath should create descending order") {
        val stringPath = mockk<StringPath>()
        every { stringPath.desc() } returns mockk()

        Expression.DESC.order(stringPath)
            .shouldBeInstanceOf<com.querydsl.core.types.OrderSpecifier<*>>()
    }

    test("ASC order for NumberPath should create ascending order") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.asc() } returns mockk()

        Expression.ASC.order(numberPath)
            .shouldBeInstanceOf<com.querydsl.core.types.OrderSpecifier<*>>()
    }

    test("DESC order for NumberPath should create descending order") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.desc() } returns mockk()

        Expression.DESC.order(numberPath)
            .shouldBeInstanceOf<com.querydsl.core.types.OrderSpecifier<*>>()
    }

    test("ASC where for StringPath on first page should use goe") {
        val stringPath = mockk<StringPath>()
        every { stringPath.goe(any<String>()) } returns mockk()

        Expression.ASC.where(stringPath, 0, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }

    test("ASC where for StringPath on subsequent page should use gt") {
        val stringPath = mockk<StringPath>()
        every { stringPath.gt(any<String>()) } returns mockk()

        Expression.ASC.where(stringPath, 1, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }

    test("DESC where for StringPath on first page should use loe") {
        val stringPath = mockk<StringPath>()
        every { stringPath.loe(any<String>()) } returns mockk()

        Expression.DESC.where(stringPath, 0, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }

    test("DESC where for StringPath on subsequent page should use lt") {
        val stringPath = mockk<StringPath>()
        every { stringPath.lt(any<String>()) } returns mockk()

        Expression.DESC.where(stringPath, 1, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }

    test("ASC where for NumberPath on first page should use goe") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.goe(any<Long>()) } returns mockk()

        Expression.ASC.where(numberPath, 0, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }

    test("ASC where for NumberPath on subsequent page should use gt") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.gt(any<Long>()) } returns mockk()

        Expression.ASC.where(numberPath, 1, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }

    test("DESC where for NumberPath on first page should use loe") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.loe(any<Long>()) } returns mockk()

        Expression.DESC.where(numberPath, 0, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }

    test("DESC where for NumberPath on subsequent page should use lt") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.lt(any<Long>()) } returns mockk()

        Expression.DESC.where(numberPath, 1, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()
    }
})
