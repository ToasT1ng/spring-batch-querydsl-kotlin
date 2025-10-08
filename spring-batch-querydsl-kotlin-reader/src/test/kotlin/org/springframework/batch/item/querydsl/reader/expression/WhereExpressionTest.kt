package org.springframework.batch.item.querydsl.reader.expression

import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class WhereExpressionTest : FunSpec({

    test("GT expression for StringPath on first page should use goe") {
        val stringPath = mockk<StringPath>()
        every { stringPath.goe(any<String>()) } returns mockk()

        WhereExpression.GT.expression(stringPath, 0, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { stringPath.goe("test") }
    }

    test("GT expression for StringPath on subsequent page should use gt") {
        val stringPath = mockk<StringPath>()
        every { stringPath.gt(any<String>()) } returns mockk()

        WhereExpression.GT.expression(stringPath, 1, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { stringPath.gt("test") }
    }

    test("LT expression for StringPath on first page should use loe") {
        val stringPath = mockk<StringPath>()
        every { stringPath.loe(any<String>()) } returns mockk()

        WhereExpression.LT.expression(stringPath, 0, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { stringPath.loe("test") }
    }

    test("LT expression for StringPath on subsequent page should use lt") {
        val stringPath = mockk<StringPath>()
        every { stringPath.lt(any<String>()) } returns mockk()

        WhereExpression.LT.expression(stringPath, 1, "test")
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { stringPath.lt("test") }
    }

    test("GT expression for NumberPath on first page should use goe") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.goe(any<Long>()) } returns mockk()

        WhereExpression.GT.expression(numberPath, 0, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { numberPath.goe(100L) }
    }

    test("GT expression for NumberPath on subsequent page should use gt") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.gt(any<Long>()) } returns mockk()

        WhereExpression.GT.expression(numberPath, 1, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { numberPath.gt(100L) }
    }

    test("LT expression for NumberPath on first page should use loe") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.loe(any<Long>()) } returns mockk()

        WhereExpression.LT.expression(numberPath, 0, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { numberPath.loe(100L) }
    }

    test("LT expression for NumberPath on subsequent page should use lt") {
        val numberPath = mockk<NumberPath<Long>>()
        every { numberPath.lt(any<Long>()) } returns mockk()

        WhereExpression.LT.expression(numberPath, 1, 100L)
            .shouldBeInstanceOf<com.querydsl.core.types.dsl.BooleanExpression>()

        verify { numberPath.lt(100L) }
    }
})
